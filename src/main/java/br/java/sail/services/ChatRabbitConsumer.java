package br.java.sail.services;

import br.java.sail.dtos.ChatMessageEvent;
import br.java.sail.dtos.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ChatRabbitConsumer {

    private final ChatService chatService;
    private final GeminiExpenseService geminiExpenseService;
    private final ChatNotificationService chatNotificationService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {"${rabbit.queue}"})
    public void consume(@Payload Message message) {
        ChatMessageEvent event = objectMapper.readValue(message.getPayload().toString(), ChatMessageEvent.class);

        String assistantContent = geminiExpenseService.replyFor(event.content());

        String standard = "{\"message\":\"Sucesso\",\"error\":false,\"data\": " +  assistantContent + "}";
        chatService.saveAssistantMessage(event.userId(), standard);
        chatNotificationService.notifyUser(
                event.userId(), "Nova mensagem gerada."
        );
    }
}
