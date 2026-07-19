package br.java.sail.implementations;

import br.java.sail.dtos.StandardResponse;
import br.java.sail.exceptions.NotFoundException;
import br.java.sail.usecases.IsWorkingUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class IsWorkingImplementation implements IsWorkingUseCase {

    /**
     * this method allows test standard return of api and verify the created custom exceptions
     * u can change the type of return commenting the code
     * @return
     */
    @Override
    public ResponseEntity<StandardResponse<?>> execute() {

        // when success
//        String a  = "a";
//        ResponseEntity<StandardResponse<?>> standardResponseResponseEntity =
//                new ResponseEntity<>(new StandardResponse<>("Sucesso", false, a), HttpStatus.OK);
//        return standardResponseResponseEntity;

        // when error
        throw new NotFoundException("Não encontrado");

    }
}
