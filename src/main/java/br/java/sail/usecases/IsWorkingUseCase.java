package br.java.sail.usecases;

import br.java.sail.dtos.StandardResponse;
import org.springframework.http.ResponseEntity;

public interface IsWorkingUseCase {

    public ResponseEntity<StandardResponse<?>> execute();
}
