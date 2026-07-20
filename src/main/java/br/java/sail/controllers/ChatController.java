package br.java.sail.controllers;

import br.java.sail.dtos.ChatMessageRequest;
import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.services.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> send(@Valid @RequestBody ChatMessageRequest request) {
        return chatService.sendMessage(request);
    }
}
