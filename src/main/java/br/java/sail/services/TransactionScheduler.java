package br.java.sail.services;

import br.java.sail.entities.TransactionUser;
import br.java.sail.repositories.TransactionUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionScheduler {

    private final TransactionUserRepository transactionUserRepository;

    @Scheduled(cron = "0 0 0 1 * *")
    public void monthlyCompute() {
        computeDue();
    }

    @Transactional
    public void computeDue() {
        List<TransactionUser> pending = transactionUserRepository.findByComputadaFalse();

        if (pending.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate currentMonth = today.withDayOfMonth(1);
        int computed = 0;

        for (TransactionUser transaction : pending) {
            LocalDate startMonth = transaction.getCreatedAt().toLocalDate().withDayOfMonth(1);
            long monthsElapsed = ChronoUnit.MONTHS.between(startMonth, currentMonth);

            if (transaction.getMesParcela() <= monthsElapsed + 1) {
                transaction.setComputada(true);
                transaction.setFeitoEm(LocalDateTime.now());
                transactionUserRepository.save(transaction);
                computed++;
            }
        }

        if (computed > 0) {
            log.info("Transações computadas pelo scheduling: {}", computed);
        }
    }
}