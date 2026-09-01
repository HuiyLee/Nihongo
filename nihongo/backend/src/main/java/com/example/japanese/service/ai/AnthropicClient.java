package com.example.japanese.service.ai;

import com.example.japanese.config.AiProperties;
import com.example.japanese.exception.AiServiceException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

/**
 * Thin wrapper around Anthropic's Messages API (requirements section 38,
 * Phase 7). This is the single place that knows about the wire format, so
 * every AI feature (grammar explanation, writing correction, conversation
 * practice) shares one call/error-handling path instead of duplicating HTTP
 * logic - the same "one place" principle StudySessionService established in
 * Phase 6.
 *
 * Deliberately fails only at call time, never at startup: a server with no
 * ANTHROPIC_API_KEY configured still runs every other feature normally, and
 * only requests to the AI endpoints get a clear 503.
 */
@Slf4j
@Component
public class AnthropicClient {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final AiProperties properties;
    private final RestTemplate restTemplate;

    public AnthropicClient(AiProperties properties, RestTemplateBuilder builder) {
        this.properties = properties;
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Sends a (possibly multi-turn) conversation to Claude and returns the
     * assistant's reply text. {@code system} may be null/blank for no system
     * prompt. The first entry of {@code messages} must have role "user".
     */
    public String complete(String system, List<Message> messages) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new AiServiceException(
                    "AI features are not configured on this server (missing ANTHROPIC_API_KEY)");
        }
        if (CollectionUtils.isEmpty(messages)) {
            throw new AiServiceException("No conversation content to send to the AI service");
        }

        Request request = new Request(properties.getModel(), properties.getMaxTokens(), system, messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", properties.getApiKey());
        headers.set("anthropic-version", ANTHROPIC_VERSION);

        Response response;
        try {
            response = restTemplate.exchange(
                    API_URL, HttpMethod.POST, new HttpEntity<>(request, headers), Response.class
            ).getBody();
        } catch (RestClientException ex) {
            log.error("Anthropic API call failed: {}", ex.getMessage());
            throw new AiServiceException("The AI service is currently unavailable. Please try again later.");
        }

        if (response == null || CollectionUtils.isEmpty(response.content())) {
            throw new AiServiceException("The AI service returned an empty response");
        }
        return response.content().stream()
                .filter(block -> "text".equals(block.type()))
                .map(ContentBlock::text)
                .findFirst()
                .orElseThrow(() -> new AiServiceException("The AI service returned no text content"));
    }

    public record Message(String role, String content) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record Request(String model, @JsonProperty("max_tokens") int maxTokens, String system,
                            List<Message> messages) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Response(String id, List<ContentBlock> content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ContentBlock(String type, String text) {
    }
}
