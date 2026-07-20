package br.java.sail.dtos;

public record VaultUserResponse(
        Long id,
        String userName,
        String vaultKey,
        String token
) {
}
