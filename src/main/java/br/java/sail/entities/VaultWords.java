package br.java.sail.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vault_words")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VaultWords {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_word")
    private Long idWord;

    @Column(name = "tx_word")
    private String txWord;

}
