package br.java.sail.exceptions;

public class InvalidVaultKeyException extends RuntimeException {
    public InvalidVaultKeyException(String message) {
        super(message);
    }
}
