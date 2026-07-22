package br.java.sail.services;

import br.java.sail.dtos.GeminiExpenseResponse;
import br.java.sail.exceptions.GeminiExpenseException;
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


    @Value("${gemini.api-key}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper;
    private Client client;

    private Client client() {
        if (client == null) {
            client = Client.builder().apiKey(geminiApiKey).build();
        }
        return client;
    }

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

    public String replyFor(String userMessage) {
        for (String model : MODELS) {
            try {
                return toJson(fetchResponse(model, userMessage));
            } catch (ApiException ex) {
                if (!shouldRetry(ex)) throw ex;
            } catch (RuntimeException ex) {
                if (!shouldRetry(ex)) throw ex;
            }
        }

        throw new GeminiExpenseException("Gemini response could not be generated");
    }

    private static boolean shouldRetry(Throwable ex) {
        return ex instanceof ApiException api && isRateLimit(api)
                || ex instanceof JacksonException
                || ex instanceof IllegalStateException;
    }

    static String buildPrompt(String userMessage) {
        return """
                Extraia dados financeiros do texto do usuário e responda somente com JSON válido.

                Regras:
                - estabelecimento: use null quando não estiver explícito no texto.
                - categoria: use "Conta fixa" quando o texto indicar despesa recorrente ou conta de serviço, como luz, água, aluguel, internet, telefone ou boleto.
                - valor: preserve o valor exatamente como foi escrito, sem símbolo de moeda.
                - metodoPagamento: mude o valor para o padrão Primeira Maiúscula e corrija ortografia  (Ex: cartão de debito -> Cartão Débito) 
                - parcela: caso Exista parcela, coloque-a como um valor inteiro.
                
                se o método for Cartão de Crédito, identifique se foi um pagamento à vista ou parcelado 
                (e caso seja especificado adicionar parcelas: como nova chave valor. Se não for especificado, null)

                Formato esperado:
                {"estabelecimento":null,"categoria":"Conta fixa","valor":"89,90","metodoPagamento":"pix"}
                ou                 
                {"estabelecimento":"Casas Bahia","categoria":"Eletrodomésticos","valor":"1889,90","metodoPagamento":"Cartão Crédito","parcelas": "10"}

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
            throw new GeminiExpenseException("Gemini returned an invalid response");
        }

        return objectMapper.readValue(text, GeminiExpenseResponse.class);
    }

    private String toJson(GeminiExpenseResponse response) {
        return objectMapper.writeValueAsString(response);
    }

    private static Map<String, Object> responseSchema() {
        Map<String, Object> type = Map.of(
                "type", "object",
                "properties", Map.of(
                        "estabelecimento", nullableStringSchema("Nome do estabelecimento, se houver."),
                        "categoria", nullableStringSchema("Categoria financeira da despesa."),
                        "valor", nullableStringSchema("Valor monetário exatamente como foi escrito."),
                        "metodoPagamento", nullableStringSchema("Método de pagamento com primeira Maiúscula."),
                        "parcelas", nullableStringSchema("Quantidade de parcelas caso o método for no cŕedito, caso não haja, null e conta como transação única")
                ),
                "required", List.of("estabelecimento", "categoria", "valor", "metodoPagamento", "parcelas"),
                "additionalProperties", false,
                "propertyOrdering", List.of("estabelecimento", "categoria", "valor", "metodoPagamento", "parcelas")
        );
        return type;
    }

    private static Map<String, Object> nullableStringSchema(String description) {
        return Map.of(
                "type", List.of("string", "null"),
                "description", description
        );
    }

}
