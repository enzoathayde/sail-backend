package br.java.sail.implementations;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.VaultAuthRequest;
import br.java.sail.dtos.VaultAuthResponse;
import br.java.sail.entities.VaultUser;
import br.java.sail.exceptions.InvalidVaultKeyException;
import br.java.sail.repositories.VaultUserRepository;
import br.java.sail.security.JwtCookieFactory;
import br.java.sail.security.JwtTokenService;
import br.java.sail.security.VaultKeyCipher;
import br.java.sail.usecases.AuthenticateVaultUserUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateVaultUserImplementation implements AuthenticateVaultUserUseCase {

    private final VaultUserRepository vaultUserRepository;
    private final VaultKeyCipher vaultKeyCipher;
    private final JwtTokenService jwtTokenService;
    private final JwtCookieFactory jwtCookieFactory;

    public AuthenticateVaultUserImplementation(
            VaultUserRepository vaultUserRepository,
            VaultKeyCipher vaultKeyCipher,
            JwtTokenService jwtTokenService,
            JwtCookieFactory jwtCookieFactory
    ) {
        this.vaultUserRepository = vaultUserRepository;
        this.vaultKeyCipher = vaultKeyCipher;
        this.jwtTokenService = jwtTokenService;
        this.jwtCookieFactory = jwtCookieFactory;
    }

    @Override
    public ResponseEntity<StandardResponse<VaultAuthResponse>> execute(VaultAuthRequest request) {
        if (request == null || request.vaultKey() == null || request.vaultKey().isBlank()) {
            throw new InvalidVaultKeyException("Vault Key is required");
        }

        String fingerprint = vaultKeyCipher.fingerprint(request.vaultKey());
        VaultUser user = vaultUserRepository.findByVaultKeyFingerprint(fingerprint)
                .orElseThrow(() -> new InvalidVaultKeyException("Invalid Vault Key"));

        String token = jwtTokenService.generate(user);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookieFactory.fromToken(token).toString())
                .body(new StandardResponse<>(
                        "Authenticated",
                        false,
                        new VaultAuthResponse(user.getIdUser(), user.getUserName(), token)
                ));
    }
}
