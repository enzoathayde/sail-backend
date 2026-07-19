package br.java.sail.exceptions;

import br.java.sail.dtos.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
