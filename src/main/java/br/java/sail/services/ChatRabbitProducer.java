package br.java.sail.services;

import br.java.sail.dtos.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class ChatRabbitProducer {

    @Value("${rabbit.routing-key}")
    private String routingKeyName;

    @Value("${rabbit.exchange}")
    private String exchangeName;

    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public void publish(ChatMessageEvent event) {
        amqpTemplate.convertAndSend(exchangeName, routingKeyName, objectMapper.writeValueAsString(event));
    }


}
