package br.java.sail.services;

import br.java.sail.dtos.ChatMessageEvent;
import br.java.sail.dtos.ExpenseData;
import br.java.sail.dtos.StandardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRabbitConsumer {

    private static final String SUCCESS_MESSAGE = "Nova mensagem gerada.";
    private static final String FAILURE_MESSAGE = "Não foi possível identificar o gasto. Tente novamente.";

    private final ChatService chatService;
    private final GroqExpenseService groqExpenseService;
    private final ChatNotificationService chatNotificationService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {"${rabbit.queue}"})
    public void consume(@Payload Message message) {
        ChatMessageEvent event = objectMapper.readValue(message.getPayload().toString(), ChatMessageEvent.class);

        String payload = buildAssistantPayload(event);

        chatService.saveAssistantMessage(event.userId(), payload);
        chatNotificationService.notifyUser(event.userId(), payload);
    }

    private String buildAssistantPayload(ChatMessageEvent event) {
        try {
            String raw = groqExpenseService.replyFor(event.content());
            ExpenseData data = objectMapper.readValue(raw, ExpenseData.class);

            return objectMapper.writeValueAsString(
                    new StandardResponse<>(SUCCESS_MESSAGE, false, data)
            );
        } catch (Exception ex) {
            log.warn("Falha ao gerar resposta estruturada: {}", ex.getMessage());
        }

        return objectMapper.writeValueAsString(
                new StandardResponse<>(FAILURE_MESSAGE, true, null)
        );
    }
}