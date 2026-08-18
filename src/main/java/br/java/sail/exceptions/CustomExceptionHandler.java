package br.java.sail.exceptions;

import br.java.sail.dtos.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<StandardResponse<?>> notFound(NotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());

        return new ResponseEntity<>(
                new StandardResponse<>("Falha ao encontrar recurso", true, null)
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<StandardResponse<?>> createObjectError(CreateSecurityObjectException ex) {
        log.warn("Erro ao criar recurso: {}", ex.getMessage());

        return new ResponseEntity<>(
                new StandardResponse<>("Falha ao criar recurso.", true, null)
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<StandardResponse<?>> invalidVaultKey(InvalidVaultKeyException ex) {
        log.warn("Credenciais inválidas: {}", ex.getMessage());

        return new ResponseEntity<>(
                new StandardResponse<>("Credenciais inválidas", true, null)
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<StandardResponse<?>> validationBodyError(MethodArgumentNotValidException ex) {
        var message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Erro de validação: {}", message);

        return new ResponseEntity<>(
                new StandardResponse<>("Falha ao capturar os parâmetros da requisição. Por favor, tente novamente.", true, message),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<StandardResponse<?>> paramAndPathError(HandlerMethodValidationException ex) {
        var message = ex.getMessage();

        return new ResponseEntity<>(
                new StandardResponse<>("Falha ao capturar os parâmetros da requisição. Por favor, tente novamente.", true, message),
                HttpStatus.BAD_REQUEST);
    }
}
