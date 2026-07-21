package br.java.sail.services;

import br.java.sail.dtos.ChatMessageResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(Long userId, String payload) {
        messagingTemplate.convertAndSend("/topic/users/" + userId, payload);
    }
}
