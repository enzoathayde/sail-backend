package br.java.sail.implementations;

import br.java.sail.dtos.ChatMessageDecisionRequest;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.entities.ChatMessage;
import br.java.sail.entities.VaultUser;
import br.java.sail.exceptions.NotFoundException;
import br.java.sail.repositories.ChatMessageRepository;
import br.java.sail.repositories.VaultUserRepository;
import br.java.sail.usecases.UpdateChatMessageDecisionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UpdateChatMessageDecisionImplementation implements UpdateChatMessageDecisionUseCase {

    private final ChatMessageRepository chatMessageRepository;
    private final VaultUserRepository vaultUserRepository;

    public UpdateChatMessageDecisionImplementation(
            ChatMessageRepository chatMessageRepository,
            VaultUserRepository vaultUserRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.vaultUserRepository = vaultUserRepository;
    }

    @Override
    public ResponseEntity<ChatMessageResponse> execute(Long messageId, ChatMessageDecisionRequest request) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        VaultUser user = vaultUserRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));

        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Mensagem não encontrada."));

        if (!message.getUserId().equals(user.getIdUser())) {
            throw new NotFoundException("Mensagem não encontrada.");
        }

        message.setAccepted(request.accepted());
        ChatMessage saved = chatMessageRepository.save(message);

        return ResponseEntity.ok(new ChatMessageResponse(
                saved.getId(),
                saved.getUserId(),
                saved.getSender().name(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.getAccepted()
        ));
    }
}
