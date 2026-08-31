package br.java.sail.dtos;

import jakarta.validation.constraints.NotNull;

public record ChatMessageDecisionRequest(
        @NotNull Boolean accepted
) {
}
