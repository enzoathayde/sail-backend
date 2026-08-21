package br.java.sail.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long userId,
        String estabelecimento,
        String categoria,
        String metodoPagamento,
        Long valorCents,
        BigDecimal valorReal,
        boolean computada,
        LocalDateTime feitoEm,
        Integer mesParcela,
        Integer parcelasTotais,
        LocalDateTime createdAt
) {
}