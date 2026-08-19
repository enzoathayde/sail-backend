package br.java.sail.implementations;

import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.dtos.StandardResponse;
import br.java.sail.entities.ChatMessage;
import br.java.sail.entities.VaultUser;
import br.java.sail.exceptions.NotFoundException;
import br.java.sail.repositories.ChatMessageRepository;
import br.java.sail.repositories.VaultUserRepository;
import br.java.sail.usecases.GetChatMessagesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetChatMessagesImplementation implements GetChatMessagesUseCase {

    private final ChatMessageRepository chatMessageRepository;
    private final VaultUserRepository vaultUserRepository;

    public GetChatMessagesImplementation(
            ChatMessageRepository chatMessageRepository,
            VaultUserRepository vaultUserRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.vaultUserRepository = vaultUserRepository;
    }

    @Override
    public ResponseEntity<StandardResponse<List<ChatMessageResponse>>> execute() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        VaultUser user = vaultUserRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));

        List<ChatMessageResponse> messages = chatMessageRepository
                .findByUserIdOrderByCreatedAtAsc(user.getIdUser())
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(new StandardResponse<>(
                "Histórico de mensagens",
                false,
                messages
        ));
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getUserId(),
                message.getSender().name(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}