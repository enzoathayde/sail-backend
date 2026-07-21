package br.java.sail.services;

import br.java.sail.dtos.ChatMessageEvent;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.entities.ChatMessage;
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
    private final MockAiResponseService mockAiResponseService;
    private final ChatNotificationService chatNotificationService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = {"${rabbit.queue}"})
    public void consume(@Payload Message message) {

        try {
            ChatMessageEvent event = objectMapper.readValue(message.getPayload().toString(), ChatMessageEvent.class);

            Thread.sleep(2000);
            String assistantContent = mockAiResponseService.replyFor(event.content());
            ChatMessage saved = chatService.saveAssistantMessage(event.userId(), assistantContent);
            chatNotificationService.notifyUser(
                    event.userId(),"Nova mensagem gerada."
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chat consumer interrupted", ex);
        }
    }
}
