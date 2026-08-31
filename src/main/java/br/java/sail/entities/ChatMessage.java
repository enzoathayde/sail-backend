package br.java.sail.entities;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.enums.ChatSender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_messages_user_id", columnList = "user_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender", nullable = false, length = 16)
    private ChatSender sender;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted")
    private Boolean accepted;

    public ChatMessage(Long userId, ChatSender sender, String content) {
        this.userId = userId;
        this.sender = sender;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }


}
