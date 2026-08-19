package br.java.sail.usecases;

import br.java.sail.dtos.ChatMessageResponse;
import br.java.sail.dtos.StandardResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GetChatMessagesUseCase {

    ResponseEntity<StandardResponse<List<ChatMessageResponse>>> execute();
}