package br.java.sail.services;

import br.java.sail.dtos.GeminiExpenseResponse;
import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiExpenseService {

    private static final List<String> MODELS = List.of(
            "gemini-3.5-flash",
            "gemini-3.5-flash-lite",
            "gemini-3.1-flash-lite"
    );

    private static final GenerateContentConfig GENERATION_CONFIG = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .candidateCount(1)
            .responseJsonSchema(responseSchema())
            .build();

    private final ObjectMapper objectMapper;
    private Client client;

    public String replyFor(String userMessage) {
        for (int index = 0; index < MODELS.size(); index++) {
            String model = MODELS.get(index);

            try {
                return toJson(fetchResponse(model, userMessage));
            } catch (ApiException ex) {
                if (isRateLimit(ex) && index < MODELS.size() - 1) {
                    continue;
                }
                throw ex;
            } catch (RuntimeException ex) {
                if ((ex instanceof JacksonException || ex instanceof IllegalStateException) && index < MODELS.size() - 1) {
                    continue;
                }
                throw ex;
            }
        }

        throw new IllegalStateException("Gemini response could not be generated");
    }

    static String buildPrompt(String userMessage) {
        return """
                Extraia dados financeiros do texto do usuário e responda somente com JSON válido.

                Regras:
                - estabelecimento: use null quando não estiver explícito no texto.
                - categoria: use "Conta fixa" quando o texto indicar despesa recorrente ou conta de serviço, como luz, água, aluguel, internet, telefone ou boleto.
                - valor: preserve o valor exatamente como foi escrito, sem símbolo de moeda.
                - metodoPagamento: normalize para minúsculas quando houver método de pagamento; use null quando não houver.

                Formato esperado:
                {"estabelecimento":null,"categoria":"Conta fixa","valor":"89,90","metodoPagamento":"pix"}

                Texto:
                %s
                """.formatted(userMessage);
    }

    static boolean isRateLimit(ApiException exception) {
        String message = exception.message().toLowerCase(Locale.ROOT);
        return exception.code() == 429
                || "RESOURCE_EXHAUSTED".equalsIgnoreCase(exception.status())
                || message.contains("requests per minute")
                || message.contains("requestsperminute");
    }

    GeminiExpenseResponse fetchResponse(String model, String userMessage) {
        GenerateContentResponse response = client().models.generateContent(model, buildPrompt(userMessage), GENERATION_CONFIG);
        String text = response.text();

        if (text == null || text.isBlank()) {
            throw new IllegalStateException("Gemini returned an empty response");
        }

        return normalize(objectMapper.readValue(text, GeminiExpenseResponse.class));
    }

    static GeminiExpenseResponse normalize(GeminiExpenseResponse response) {
        return new GeminiExpenseResponse(
                blankToNull(response.estabelecimento()),
                normalizeCategory(response.categoria()),
                blankToNull(response.valor()),
                normalizePaymentMethod(response.metodoPagamento())
        );
    }

    private String toJson(GeminiExpenseResponse response) {
        return objectMapper.writeValueAsString(response);
    }

    private static String blankToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeCategory(String value) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            return null;
        }

        if ("conta fixa".equalsIgnoreCase(normalized)) {
            return "Conta fixa";
        }

        return normalized;
    }

    private static String normalizePaymentMethod(String value) {
        String normalized = blankToNull(value);
        return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
    }

    private static Map<String, Object> responseSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "estabelecimento", nullableStringSchema("Nome do estabelecimento, se houver."),
                        "categoria", nullableStringSchema("Categoria financeira da despesa."),
                        "valor", nullableStringSchema("Valor monetário exatamente como foi escrito."),
                        "metodoPagamento", nullableStringSchema("Método de pagamento em minúsculas.")
                ),
                "required", List.of("estabelecimento", "categoria", "valor", "metodoPagamento"),
                "additionalProperties", false,
                "propertyOrdering", List.of("estabelecimento", "categoria", "valor", "metodoPagamento")
        );
    }

    private static Map<String, Object> nullableStringSchema(String description) {
        return Map.of(
                "type", List.of("string", "null"),
                "description", description
        );
    }

    @Value("${gemini.api-key}")
    private String geminiApiKey;

    private Client client() {
        if (client == null) {
            client = Client.builder().apiKey(geminiApiKey).build();
        }
        return client;
    }
}
