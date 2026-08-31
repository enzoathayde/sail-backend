package br.java.sail.implementations;

import br.java.sail.security.JwtCookieFactory;
import br.java.sail.usecases.LogoutVaultUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutVaultUserImplementation implements LogoutVaultUserUseCase {

    private final JwtCookieFactory jwtCookieFactory;

    @Override
    public ResponseEntity<Void> execute() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, jwtCookieFactory.clear().toString())
                .build();
    }
}
