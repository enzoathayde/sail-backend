package br.java.sail.dtos;

public record GeminiExpenseResponse(
        String estabelecimento,
        String categoria,
        String valor,
        String metodoPagamento,
        String parcelas
) {
}
