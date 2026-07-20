package br.java.sail.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "vault_users",
        indexes = {
                @Index(name = "idx_vault_users_user_name", columnList = "user_name")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VaultUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "user_name", nullable = false, length = 64)
    private String userName;

    @Column(name = "vault_key", nullable = false, columnDefinition = "text")
    private String vaultKey;

    @Column(name = "vault_key_fingerprint", nullable = false, length = 64)
    private String vaultKeyFingerprint;

    public VaultUser(String userName, String vaultKey, String vaultKeyFingerprint) {
        this.userName = userName;
        this.vaultKey = vaultKey;
        this.vaultKeyFingerprint = vaultKeyFingerprint;
    }

}
