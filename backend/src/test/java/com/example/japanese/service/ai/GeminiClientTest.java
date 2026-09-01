package com.example.japanese.service.ai;

import com.example.japanese.config.AiProperties;
import com.example.japanese.exception.AiServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit test (no Spring context) - confirms GeminiClient fails fast
 * with a clear AiServiceException, never a raw NPE/exception leak, when no
 * GEMINI_API_KEY is configured. This is the exact state the app is in out
 * of the box (application.yml defaults app.ai.api-key to blank).
 */
class GeminiClientTest {

    @Test
    void complete_withNoApiKeyConfigured_throwsAiServiceException() {
        AiProperties properties = new AiProperties();
        properties.setApiKey("");
        properties.setModel("gemini-2.5-flash");
        properties.setMaxTokens(1024);

        GeminiClient client = new GeminiClient(properties, new RestTemplateBuilder());

        assertThatThrownBy(() ->
                client.complete("system prompt", List.of(new GeminiClient.Message("user", "hello")))
        ).isInstanceOf(AiServiceException.class)
                .hasMessageContaining("not configured");
    }

    @Test
    void complete_withNoMessages_throwsAiServiceException() {
        AiProperties properties = new AiProperties();
        properties.setApiKey("fake-key-for-test");
        properties.setModel("gemini-2.5-flash");
        properties.setMaxTokens(1024);

        GeminiClient client = new GeminiClient(properties, new RestTemplateBuilder());

        assertThatThrownBy(() -> client.complete("system prompt", List.of()))
                .isInstanceOf(AiServiceException.class);
    }
}
