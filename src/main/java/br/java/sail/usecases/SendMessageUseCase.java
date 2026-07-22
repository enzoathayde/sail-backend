package br.java.sail.usecases;

import br.java.sail.dtos.ChatMessageRequest;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.VaultAuthResponse;
import org.springframework.http.ResponseEntity;

public interface SendMessageUseCase {

    public ResponseEntity<StandardResponse<ChatMessageResponse>> execute(ChatMessageRequest request);
}
