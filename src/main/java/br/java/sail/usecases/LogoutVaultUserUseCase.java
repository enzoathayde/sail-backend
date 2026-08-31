package br.java.sail.usecases;

import org.springframework.http.ResponseEntity;

public interface LogoutVaultUserUseCase {
    ResponseEntity<Void> execute();
}
