package br.java.sail.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenRouterExpenseService {

    private final ObjectMapper objectMapper;

    @Value("${openrouter.api-key}")
    private String openRouterApiKey;

    @Value("${openrouter.base-url}")
    private String baseUrl;

    @Value("${openrouter.models}")
    private String configuredModels;

    public String replyFor(String text) {
        String prompt = buildPrompt(text);

        for (String model : models()) {
            try {
                String content = chatContent(model, prompt);

                if (content != null && !content.isBlank()) {
                    return content;
                }

                log.warn("OpenRouter model '{}' retornou uma resposta vazia, indo para o próximo modelo", model);
            } catch (RuntimeException ex) {
                log.warn("OpenRouter model '{}' com falha, indo para o próximo modelo.", model, ex);
            }
        }

        throw new IllegalStateException("OpenRouter response could not be generated with any of the available models");
    }

    private List<String> models() {
        return List.of(configuredModels.split(","));
    }

    private String chatContent(String model, String prompt) {
        String body = RestClient.create(baseUrl).post()
                .uri("/api/v1/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openRouterApiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("HTTP-Referer", "http://localhost:8090")
                .header("X-Title", "Sail")
                .body(Map.of(
                        "model", model,
                        "messages", List.of(Map.of("role", "user", "content", prompt))
                ))
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(body);
        JsonNode content = root.path("choices").path(0).path("message").path("content");

        if (content.isMissingNode() || content.isNull()) {
            return null;
        }

        return content.asText();
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
}