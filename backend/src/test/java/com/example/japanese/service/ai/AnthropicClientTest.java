package com.example.japanese.service.ai;

import com.example.japanese.config.AiProperties;
import com.example.japanese.exception.AiServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pure unit test (no Spring context) - confirms AnthropicClient fails fast
 * with a clear AiServiceException, never a raw NPE/exception leak, when no
 * ANTHROPIC_API_KEY is configured. This is the exact state the app is in out
 * of the box (application.yml defaults app.ai.api-key to blank).
 */
class AnthropicClientTest {

    @Test
    void complete_withNoApiKeyConfigured_throwsAiServiceException() {
        AiProperties properties = new AiProperties();
        properties.setApiKey("");
        properties.setModel("claude-sonnet-4-5");
        properties.setMaxTokens(1024);

        AnthropicClient client = new AnthropicClient(properties, new RestTemplateBuilder());

        assertThatThrownBy(() ->
                client.complete("system prompt", List.of(new AnthropicClient.Message("user", "hello")))
        ).isInstanceOf(AiServiceException.class)
                .hasMessageContaining("not configured");
    }

    @Test
    void complete_withNoMessages_throwsAiServiceException() {
        AiProperties properties = new AiProperties();
        properties.setApiKey("sk-fake-key-for-test");
        properties.setModel("claude-sonnet-4-5");
        properties.setMaxTokens(1024);

        AnthropicClient client = new AnthropicClient(properties, new RestTemplateBuilder());

        assertThatThrownBy(() -> client.complete("system prompt", List.of()))
                .isInstanceOf(AiServiceException.class);
    }
}
