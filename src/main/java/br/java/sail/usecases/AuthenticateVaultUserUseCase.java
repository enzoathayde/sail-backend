package br.java.sail.usecases;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.VaultAuthRequest;
import br.java.sail.dtos.VaultAuthResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticateVaultUserUseCase {

    ResponseEntity<StandardResponse<VaultAuthResponse>> execute(VaultAuthRequest request);
}
