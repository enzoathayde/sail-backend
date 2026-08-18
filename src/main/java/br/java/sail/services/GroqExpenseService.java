package br.java.sail.services;

import io.github.frankleyrocha.groqapi.GroqApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqExpenseService {


    @Value("${groq.api-key}")
    private String groqApiKey;

    private static final List<String> models = List.of("llama-3.3-70b-versatile", "llama-3.1-8b-instant");


    public String replyFor(String text) {

        String prompt = buildPrompt(text);

        for (String model : models) {
            try {
                GroqApi api = new GroqApi(groqApiKey);

                String content = api.completions(model, prompt);

                if (content != null && !content.isBlank()) {
                    return content;
                }

                log.warn("Groq model '{}' retornou uma resposta vazia, indo para o pŕoximo moddelo", model);
            } catch (RuntimeException ex) {
                log.warn("Groq model '{}' com falha, indo para o próximo modelo.", model, ex);
            }
        }

        throw new IllegalStateException("Groq response could not be generated with any of the available models");
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