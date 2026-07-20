package br.java.sail.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
        @NotNull Long userId,
        @NotBlank String content
) {
}
