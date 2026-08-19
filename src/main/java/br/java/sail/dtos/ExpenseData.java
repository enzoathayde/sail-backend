package br.java.sail.dtos;

import tools.jackson.databind.JsonNode;

public record ExpenseData(
        String estabelecimento,
        String categoria,
        String valor,
        String metodoPagamento,
        JsonNode parcelas
) {
}
