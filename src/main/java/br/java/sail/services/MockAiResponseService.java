package br.java.sail.services;

import org.springframework.stereotype.Service;

@Service
public class MockAiResponseService {

    public String replyFor(String userMessage) {
        return "Mock AI response: " + userMessage;
    }
}
