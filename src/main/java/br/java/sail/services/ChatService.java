package br.java.sail.services;

import br.java.sail.dtos.*;
import br.java.sail.entities.ChatMessage;
import br.java.sail.entities.ChatSender;
import br.java.sail.repositories.ChatMessageRepository;
import br.java.sail.usecases.SendMessageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService implements SendMessageUseCase {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRabbitProducer chatRabbitProducer;

    public ChatMessage saveAssistantMessage(Long userId, String content) {
        return chatMessageRepository.save(new ChatMessage(
                null,
                userId,
                ChatSender.ASSISTANT,
                content,
                LocalDateTime.now()
        ));
    }

    @Override
    public ResponseEntity<StandardResponse<ChatMessageResponse>> execute(ChatMessageRequest request) {
        ChatMessage saved = chatMessageRepository.save(new ChatMessage(
                request.userId(),
                ChatSender.USER,
                request.content(),
                LocalDateTime.now()
        ));

        chatRabbitProducer.publish(new ChatMessageEvent(saved.getId(), saved.getUserId(), saved.getContent()));

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new StandardResponse<>( "Mensagem encaminhada com sucesso", false,new ChatMessageResponse(
                        saved.getId(),
                        saved.getUserId(),
                        saved.getSender().name(),
                        saved.getContent(),
                        saved.getCreatedAt()
                )));
    }
}
