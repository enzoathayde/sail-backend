package br.java.sail.controllers;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.dtos.TransactionRequest;
import br.java.sail.dtos.TransactionResponse;
import br.java.sail.usecases.CreateTransactionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;

    @PostMapping
    public ResponseEntity<StandardResponse<List<TransactionResponse>>> create(@Valid @RequestBody TransactionRequest request) {
        return createTransactionUseCase.execute(request);
    }
}