package br.java.sail.implementations;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.TransactionRequest;
import br.java.sail.dtos.TransactionResponse;
import br.java.sail.entities.TransactionUser;
import br.java.sail.entities.VaultUser;
import br.java.sail.exceptions.NotFoundException;
import br.java.sail.repositories.TransactionUserRepository;
import br.java.sail.repositories.VaultUserRepository;
import br.java.sail.services.TransactionScheduler;
import br.java.sail.usecases.CreateTransactionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateTransactionImplementation implements CreateTransactionUseCase {

    private static final int ONE_HUNDRED = 100;

    private final VaultUserRepository vaultUserRepository;
    private final TransactionUserRepository transactionUserRepository;
    private final TransactionScheduler transactionScheduler;

    public CreateTransactionImplementation(
            VaultUserRepository vaultUserRepository,
            TransactionUserRepository transactionUserRepository,
            TransactionScheduler transactionScheduler
    ) {
        this.vaultUserRepository = vaultUserRepository;
        this.transactionUserRepository = transactionUserRepository;
        this.transactionScheduler = transactionScheduler;
    }

    @Override
    public ResponseEntity<StandardResponse<List<TransactionResponse>>> execute(TransactionRequest request) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        VaultUser user = vaultUserRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));

        long totalCents = toCents(request.valor());
        int parcelas = request.parcelas() != null ? request.parcelas() : 1;

        List<TransactionUser> saved = new ArrayList<>(parcelas);

        if (parcelas <= 1) {
            saved.add(transactionUserRepository.save(new TransactionUser(
                    user.getIdUser(),
                    request.estabelecimento(),
                    request.categoria(),
                    request.metodoPagamento(),
                    totalCents,
                    1,
                    1
            )));
        } else {
            long base = totalCents / parcelas;
            long remainder = totalCents % parcelas;

            for (int installment = 1; installment <= parcelas; installment++) {
                long installmentCents = base + (installment == 1 ? remainder : 0);

                saved.add(transactionUserRepository.save(new TransactionUser(
                        user.getIdUser(),
                        request.estabelecimento(),
                        request.categoria(),
                        request.metodoPagamento(),
                        installmentCents,
                        installment,
                        parcelas
                )));
            }
        }

        transactionScheduler.computeDue();

        List<TransactionResponse> responses = saved.stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse<>(
                        "Transação registrada",
                        false,
                        responses
                ));
    }

    private long toCents(String valor) {
        String normalized = valor.replace(",", ".").trim();

        return new BigDecimal(normalized)
                .multiply(BigDecimal.valueOf(ONE_HUNDRED))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private TransactionResponse toResponse(TransactionUser transaction) {
        BigDecimal valorReal = BigDecimal.valueOf(transaction.getValor())
                .divide(BigDecimal.valueOf(ONE_HUNDRED), 2, RoundingMode.HALF_UP);

        return new TransactionResponse(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getEstabelecimento(),
                transaction.getCategoria(),
                transaction.getMetodoPagamento(),
                transaction.getValor(),
                valorReal,
                transaction.isComputada(),
                transaction.getFeitoEm(),
                transaction.getMesParcela(),
                transaction.getParcelasTotais(),
                transaction.getCreatedAt()
        );
    }
}