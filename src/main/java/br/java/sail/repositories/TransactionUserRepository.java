package br.java.sail.repositories;

import br.java.sail.entities.TransactionUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionUserRepository extends JpaRepository<TransactionUser, Long> {

    List<TransactionUser> findByUserId(Long userId);

    List<TransactionUser> findByComputadaFalse();
}