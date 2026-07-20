package br.java.sail.repositories;

import br.java.sail.entities.VaultWords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VaultWordsRepository extends JpaRepository<VaultWords, Long> {

    @Query(value = "select * from vault_words order by random() limit 7", nativeQuery = true)
    List<VaultWords> findSevenRandomWords();
}
