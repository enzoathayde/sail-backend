package br.java.sail.usecases;

import br.java.sail.dtos.MeResponse;
import br.java.sail.dtos.StandardResponse;
import org.springframework.http.ResponseEntity;

public interface GetCurrentVaultUserUseCase {

    ResponseEntity<StandardResponse<MeResponse>> execute();
}