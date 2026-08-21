package br.java.sail.usecases;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.TransactionRequest;
import br.java.sail.dtos.TransactionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CreateTransactionUseCase {

    ResponseEntity<StandardResponse<List<TransactionResponse>>> execute(TransactionRequest request);
}