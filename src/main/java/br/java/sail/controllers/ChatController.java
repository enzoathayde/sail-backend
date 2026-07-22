package br.java.sail.controllers;

import br.java.sail.dtos.ChatMessageRequest;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.dtos.StandardResponse;
import br.java.sail.services.ChatService;
import br.java.sail.usecases.SendMessageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SendMessageUseCase sendMessageUseCase;

    @PostMapping("/messages")
    public ResponseEntity<StandardResponse<ChatMessageResponse>> send(@Valid @RequestBody ChatMessageRequest request) {
        return sendMessageUseCase.execute(request);
    }
}
