package br.java.sail.services;

import br.java.sail.dtos.GeminiExpenseResponse;
import io.github.frankleyrocha.groqapi.GroqApi;
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
public class GroqExpenseService {


    @Value("${groq.api-key}")
    private String groqApiKey;

    private final ObjectMapper objectMapper;
    private static final List<String> models = List.of("llama-3.3-70b-versatile", "llama-3.1-8b-instant");


    public String replyFor(String text) {

        for (String model : models) {

            GroqApi api = new GroqApi(groqApiKey);

            return api.completions(
                    model,
                    buildPrompt(text)
            );
        }

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
