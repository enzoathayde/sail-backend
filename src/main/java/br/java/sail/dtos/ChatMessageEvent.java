package br.java.sail.dtos;

public record ChatMessageEvent(
        Long messageId,
        Long userId,
        String content
) {
}
