package br.java.sail.services;

import br.java.sail.dtos.ChatMessageEvent;
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
        chatService.saveAssistantMessage(event.userId(), assistantContent);
        chatNotificationService.notifyUser(
                event.userId(), "Nova mensagem gerada."
        );
    }
}
