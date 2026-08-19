package br.java.sail.controllers;

import br.java.sail.dtos.ChatMessageRequest;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.dtos.StandardResponse;
import br.java.sail.services.ChatService;
import br.java.sail.usecases.GetChatMessagesUseCase;
import br.java.sail.usecases.SendMessageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SendMessageUseCase sendMessageUseCase;
    private final GetChatMessagesUseCase getChatMessagesUseCase;

    @PostMapping("/messages")
    public ResponseEntity<StandardResponse<ChatMessageResponse>> send(@Valid @RequestBody ChatMessageRequest request) {
        return sendMessageUseCase.execute(request);
    }

    @GetMapping("/messages")
    public ResponseEntity<StandardResponse<List<ChatMessageResponse>>> history() {
        return getChatMessagesUseCase.execute();
    }
}