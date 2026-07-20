package br.java.sail.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VaultKeyCipherTest {

    @Test
    void encryptAndDecryptRoundTrips() {
        VaultKeyCipher cipher = new VaultKeyCipher();
        setField(cipher, "secret", "test-secret");
        cipher.init();

        String plainText = "abacate banana carro casa dado elefante flor";
        String encrypted = cipher.encrypt(plainText);

        assertNotEquals(plainText, encrypted);
        assertEquals(plainText, cipher.decrypt(encrypted));
        assertEquals(cipher.fingerprint(plainText), cipher.fingerprint("abacate   banana carro casa dado elefante flor"));
        assertEquals(cipher.fingerprint(plainText), cipher.fingerprint("flor elefante dado casa carro banana abacate"));
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
