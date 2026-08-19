package br.java.sail.repositories;

import br.java.sail.entities.VaultUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaultUserRepository extends JpaRepository<VaultUser, Long> {

    boolean existsByVaultKeyFingerprint(String vaultKeyFingerprint);

    boolean existsByUserName(String userName);

    Optional<VaultUser> findByVaultKeyFingerprint(String vaultKeyFingerprint);

    Optional<VaultUser> findByUserName(String userName);
}
