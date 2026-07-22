package br.java.sail.services;

import br.java.sail.dtos.GeminiExpenseResponse;
import com.google.genai.errors.ApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class GeminiExpenseServiceTest {

    @Test
    void buildsExpensePromptWithExpectedRules() {
        String prompt = GeminiExpenseService.buildPrompt("gastei 89,90 de luz no pix");

        assertTrue(prompt.contains("Conta fixa"));
        assertTrue(prompt.contains("gastei 89,90 de luz no pix"));
        assertTrue(prompt.contains("\"metodoPagamento\":\"pix\""));
    }

    @Test
    void normalizesExpectedExpenseResponse() {
        GeminiExpenseResponse response = GeminiExpenseService.normalize(
                new GeminiExpenseResponse("  ", "conta fixa", " 89,90 ", "PIX")
        );

        assertNull(response.estabelecimento());
        assertEquals("Conta fixa", response.categoria());
        assertEquals("89,90", response.valor());
        assertEquals("pix", response.metodoPagamento());
    }

    @Test
    void detectsRateLimitResponses() {
        ApiException exception = new ApiException(429, "RESOURCE_EXHAUSTED", "RequestsPerMinute limit reached");

        assertTrue(GeminiExpenseService.isRateLimit(exception));
    }
}
