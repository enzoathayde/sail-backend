package br.java.sail.implementations;

import br.java.sail.dtos.MeResponse;
import br.java.sail.dtos.StandardResponse;
import br.java.sail.entities.VaultUser;
import br.java.sail.exceptions.NotFoundException;
import br.java.sail.repositories.VaultUserRepository;
import br.java.sail.usecases.GetCurrentVaultUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentVaultUserImplementation implements GetCurrentVaultUserUseCase {

    private final VaultUserRepository vaultUserRepository;

    public GetCurrentVaultUserImplementation(VaultUserRepository vaultUserRepository) {
        this.vaultUserRepository = vaultUserRepository;
    }

    @Override
    public ResponseEntity<StandardResponse<MeResponse>> execute() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();

        VaultUser user = vaultUserRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));

        return ResponseEntity.ok(new StandardResponse<>(
                "Usuário autenticado",
                false,
                new MeResponse(user.getIdUser(), user.getUserName())
        ));
    }
}