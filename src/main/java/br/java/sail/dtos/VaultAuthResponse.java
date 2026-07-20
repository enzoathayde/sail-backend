package br.java.sail.dtos;

public record VaultAuthResponse(
        Long id,
        String userName,
        String token
) {
}
