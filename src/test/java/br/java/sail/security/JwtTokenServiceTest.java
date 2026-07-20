package br.java.sail.security;

import br.java.sail.entities.VaultUser;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtTokenServiceTest {

    @Test
    void generatesAndVerifiesTokenClaims() {
        JwtTokenService service = new JwtTokenService();
        setField(service, "secret", "test-secret");
        service.init();

        VaultUser user = new VaultUser("User123456", "encrypted", "abc123");
        String token = service.generate(user);

        DecodedJWT decodedJWT = service.verify(token);
        assertEquals("auth-api", decodedJWT.getIssuer());
        assertEquals("User123456", decodedJWT.getSubject());
        assertEquals("abc123", decodedJWT.getClaim("fingerprint").asString());
        assertEquals("User123456", decodedJWT.getClaim("username").asString());
    }

    private static void setField(Object target, String name, Object value) {
        try {
            var field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
