package br.java.sail.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JwtCookieFactory {

    private static final Duration MAX_AGE = Duration.ofHours(2);

    @Value("${security.cookie.name}")
    private String cookieName;

    @Value("${security.cookie.secure}")
    private boolean secure;

    @Value("${security.cookie.same-site}")
    private String sameSite;

    public String name() {
        return cookieName;
    }

    public ResponseCookie fromToken(String token) {
        return ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api")
                .maxAge(MAX_AGE)
                .build();
    }

    public ResponseCookie clear() {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/api")
                .maxAge(Duration.ZERO)
                .build();
    }
}