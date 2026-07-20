package br.java.sail.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MockAiResponseServiceTest {

    @Test
    void buildsMockResponseFromUserMessage() {
        MockAiResponseService service = new MockAiResponseService();

        assertEquals("Mock AI response: hello", service.replyFor("hello"));
    }
}
