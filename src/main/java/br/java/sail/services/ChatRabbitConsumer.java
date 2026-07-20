package br.java.sail.services;

import br.java.sail.config.ChatRabbitConfig;
import br.java.sail.dtos.ChatMessageEvent;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.entities.ChatMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ChatRabbitConsumer {

    private final ChatService chatService;
    private final MockAiResponseService mockAiResponseService;
    private final ChatNotificationService chatNotificationService;

    public ChatRabbitConsumer(
            ChatService chatService,
            MockAiResponseService mockAiResponseService,
            ChatNotificationService chatNotificationService
    ) {
        this.chatService = chatService;
        this.mockAiResponseService = mockAiResponseService;
        this.chatNotificationService = chatNotificationService;
    }

    @RabbitListener(queues = ChatRabbitConfig.CHAT_MESSAGES_QUEUE)
    public void consume(ChatMessageEvent event) {
        try {
            Thread.sleep(2000);
            String assistantContent = mockAiResponseService.replyFor(event.content());
            ChatMessage saved = chatService.saveAssistantMessage(event.userId(), assistantContent);
            chatNotificationService.notifyUser(
                    event.userId(),
                    new ChatMessageResponse(
                            saved.getId(),
                            saved.getUserId(),
                            saved.getSender().name(),
                            saved.getContent(),
                            saved.getCreatedAt()
                    )
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Chat consumer interrupted", ex);
        }
    }
}
