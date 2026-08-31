package br.java.sail.services;

import br.java.sail.dtos.*;
import br.java.sail.entities.ChatMessage;
import br.java.sail.entities.VaultUser;
import br.java.sail.enums.ChatSender;
import br.java.sail.exceptions.NotFoundException;
import br.java.sail.repositories.ChatMessageRepository;
import br.java.sail.repositories.VaultUserRepository;
import br.java.sail.usecases.SendMessageUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService implements SendMessageUseCase {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRabbitProducer chatRabbitProducer;
    private final VaultUserRepository vaultUserRepository;

    public ChatMessage saveAssistantMessage(Long userId, String content) {
        return chatMessageRepository.save(new ChatMessage(
                null,
                userId,
                ChatSender.ASSISTANT,
                content,
                LocalDateTime.now(),
                null
        ));
    }

    @Override
    public ResponseEntity<StandardResponse<ChatMessageResponse>> execute(ChatMessageRequest request) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        VaultUser user = vaultUserRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));

        ChatMessage saved = chatMessageRepository.save(new ChatMessage(
                user.getIdUser(),
                ChatSender.USER,
                request.content()
        ));

        chatRabbitProducer.publish(new ChatMessageEvent(saved.getId(), saved.getUserId(), saved.getContent()));

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new StandardResponse<>( "Mensagem encaminhada com sucesso", false,new ChatMessageResponse(
                        saved.getId(),
                        saved.getUserId(),
                        saved.getSender().name(),
                        saved.getContent(),
                        saved.getCreatedAt(),
                        saved.getAccepted()
                )));
    }
}
