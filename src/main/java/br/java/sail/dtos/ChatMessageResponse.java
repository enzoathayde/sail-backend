package br.java.sail.dtos;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long userId,
        String sender,
        String content,
        LocalDateTime createdAt
) {
}
