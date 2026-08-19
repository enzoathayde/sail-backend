package br.java.sail.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TransactionRequest(
        String estabelecimento,
        String categoria,
        @NotBlank String valor,
        String metodoPagamento,
        @Positive Integer parcelas
) {
}