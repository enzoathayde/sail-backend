package br.java.sail.services;

import br.java.sail.dtos.ChatMessageEvent;
import br.java.sail.dtos.ChatMessageRequest;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.entities.ChatMessage;
import br.java.sail.entities.ChatSender;
import br.java.sail.repositories.ChatMessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRabbitProducer chatRabbitProducer;

    public ChatService(ChatMessageRepository chatMessageRepository, ChatRabbitProducer chatRabbitProducer) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRabbitProducer = chatRabbitProducer;
    }

    public ResponseEntity<ChatMessageResponse> sendMessage(ChatMessageRequest request) {
        ChatMessage saved = chatMessageRepository.save(new ChatMessage(
                null,
                request.userId(),
                ChatSender.USER,
                request.content(),
                LocalDateTime.now()
        ));

        chatRabbitProducer.publish(new ChatMessageEvent(saved.getId(), saved.getUserId(), saved.getContent()));

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ChatMessageResponse(
                        saved.getId(),
                        saved.getUserId(),
                        saved.getSender().name(),
                        saved.getContent(),
                        saved.getCreatedAt()
                ));
    }

    public ChatMessage saveAssistantMessage(Long userId, String content) {
        return chatMessageRepository.save(new ChatMessage(
                null,
                userId,
                ChatSender.ASSISTANT,
                content,
                LocalDateTime.now()
        ));
    }
}
