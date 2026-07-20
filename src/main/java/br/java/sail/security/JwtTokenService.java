package br.java.sail.security;

import br.java.sail.entities.VaultUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenService {

    private static final String ISSUER = "auth-api";

    @Value("${security.token.secret}")
    private String secret;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    void init() {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
    }

    public String generate(VaultUser user) {
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUserName())
                .withClaim("fingerprint", user.getVaultKeyFingerprint())
                .withClaim("username", user.getUserName())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(2, ChronoUnit.HOURS)))
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) {
        return verifier.verify(token);
    }
}
