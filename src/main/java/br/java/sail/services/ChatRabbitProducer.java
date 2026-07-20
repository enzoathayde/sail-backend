package br.java.sail.services;

import br.java.sail.config.ChatRabbitConfig;
import br.java.sail.dtos.ChatMessageEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatRabbitProducer {

    private final RabbitTemplate rabbitTemplate;

    public ChatRabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(ChatMessageEvent event) {
        rabbitTemplate.convertAndSend(ChatRabbitConfig.CHAT_MESSAGES_QUEUE, event);
    }
}
