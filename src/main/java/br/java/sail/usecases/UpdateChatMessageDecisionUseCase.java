package br.java.sail.usecases;

import br.java.sail.dtos.ChatMessageDecisionRequest;
import br.java.sail.dtos.ChatMessageResponse;
import org.springframework.http.ResponseEntity;

public interface UpdateChatMessageDecisionUseCase {
    ResponseEntity<ChatMessageResponse> execute(Long messageId, ChatMessageDecisionRequest request);
}
