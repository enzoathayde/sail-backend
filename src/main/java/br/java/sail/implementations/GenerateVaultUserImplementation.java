package br.java.sail.implementations;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.VaultUserResponse;
import br.java.sail.entities.VaultUser;
import br.java.sail.entities.VaultWords;
import br.java.sail.exceptions.CreateSecurityObjectException;
import br.java.sail.repositories.VaultUserRepository;
import br.java.sail.repositories.VaultWordsRepository;
import br.java.sail.security.VaultKeyCipher;
import br.java.sail.usecases.GenerateVaultUserUseCase;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class GenerateVaultUserImplementation implements GenerateVaultUserUseCase {

    @Value("${security.token.secret}")
    private String secret;

    private static final int WORD_COUNT = 7;
    private static final int MAX_ATTEMPTS = 1000;

    private final VaultWordsRepository vaultWordsRepository;
    private final VaultUserRepository vaultUserRepository;
    private final VaultKeyCipher vaultKeyCipher;

    public GenerateVaultUserImplementation(
            VaultWordsRepository vaultWordsRepository,
            VaultUserRepository vaultUserRepository,
            VaultKeyCipher vaultKeyCipher
    ) {
        this.vaultWordsRepository = vaultWordsRepository;
        this.vaultUserRepository = vaultUserRepository;
        this.vaultKeyCipher = vaultKeyCipher;
    }

    @Override
    public ResponseEntity<StandardResponse<VaultUserResponse>> execute() {
        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            String userName = generateUniqueUserName();
            List<String> words = generateVaultWords();
            String vaultKey = String.join(" ", words);
            String fingerprint = vaultKeyCipher.fingerprint(vaultKey);

            if (vaultUserRepository.existsByVaultKeyFingerprint(fingerprint)) {
                continue;
            }

            VaultUser saved;
            saved = vaultUserRepository.save(
                    new VaultUser(
                            userName,
                            vaultKeyCipher.encrypt(vaultKey),
                            fingerprint
                    )
            );

            String generatedToken = generateToken(fingerprint, userName);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new StandardResponse<>(
                            "Chave única gerada",
                            false,
                            new VaultUserResponse(saved.getIdUser(), saved.getUserName(), vaultKey, generatedToken)
                    ));
        }

        throw new CreateSecurityObjectException("Falha ao criar chave única");
    }

    private String generateUniqueUserName() {
        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            String userName = "User" + ThreadLocalRandom.current().nextInt(100000, 1000000);

            return userName;
        }

        throw new CreateSecurityObjectException("Falha ao criar usuário único após N tentativas. Tente novamente.");
    }

    private List<String> generateVaultWords() {
        List<VaultWords> randomWords = vaultWordsRepository.findSevenRandomWords();
        if (randomWords.size() < WORD_COUNT) {
            throw new CreateSecurityObjectException("Falha ao criar senha única. Tente novamente.");
        }

        List<String> words = new ArrayList<>(WORD_COUNT);
        for (VaultWords vaultWord : randomWords) {
            words.add(vaultWord.getTxWord());
        }

        Collections.shuffle(words);
        return words;
    }

    public String generateToken(String fingerPrint, String userName) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withClaim("fingerprint", fingerPrint)
                    .withClaim("username", userName)
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new CreateSecurityObjectException("Error while generating token {}" + exception.getMessage());
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}
