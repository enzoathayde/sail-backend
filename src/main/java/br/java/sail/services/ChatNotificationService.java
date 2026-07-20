package br.java.sail.services;

import br.java.sail.dtos.ChatMessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyUser(Long userId, ChatMessageResponse payload) {
        messagingTemplate.convertAndSend("/topic/users/" + userId, payload);
    }
}
