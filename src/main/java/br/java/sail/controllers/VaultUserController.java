package br.java.sail.controllers;

import br.java.sail.dtos.MeResponse;
import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.VaultAuthRequest;
import br.java.sail.dtos.VaultAuthResponse;
import br.java.sail.dtos.VaultUserResponse;
import br.java.sail.usecases.AuthenticateVaultUserUseCase;
import br.java.sail.usecases.GenerateVaultUserUseCase;
import br.java.sail.usecases.GetCurrentVaultUserUseCase;
import br.java.sail.usecases.LogoutVaultUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vault-users")
@RequiredArgsConstructor
public class VaultUserController {

    private final GenerateVaultUserUseCase generateVaultUserUseCase;
    private final AuthenticateVaultUserUseCase authenticateVaultUserUseCase;
    private final GetCurrentVaultUserUseCase getCurrentVaultUserUseCase;
    private final LogoutVaultUserUseCase logoutVaultUserUseCase;

    @PostMapping("/generate")
    public ResponseEntity<StandardResponse<VaultUserResponse>> generate() {
        return generateVaultUserUseCase.execute();
    }

    @PostMapping("/auth")
    public ResponseEntity<StandardResponse<VaultAuthResponse>> auth(@RequestBody VaultAuthRequest request) {
        return authenticateVaultUserUseCase.execute(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return logoutVaultUserUseCase.execute();
    }

    @GetMapping("/me")
    public ResponseEntity<StandardResponse<MeResponse>> me() {
        return getCurrentVaultUserUseCase.execute();
    }
}
