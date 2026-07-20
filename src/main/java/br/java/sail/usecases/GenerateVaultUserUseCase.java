package br.java.sail.usecases;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.VaultUserResponse;
import org.springframework.http.ResponseEntity;

public interface GenerateVaultUserUseCase {

    ResponseEntity<StandardResponse<VaultUserResponse>> execute();
}
