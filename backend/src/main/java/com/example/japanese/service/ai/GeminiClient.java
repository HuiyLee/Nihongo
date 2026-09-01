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
import java.util.stream.Collectors;

/**
 * Thin wrapper around Google's Gemini API (generateContent) - requirements
 * section 38, Phase 7. Chosen over a paid provider so the AI features work
 * on Google AI Studio's free tier (no credit card required to get a key,
 * as of when this was written - verify current terms at
 * https://aistudio.google.com/apikey since free-tier policies can change).
 *
 * This is the single place that knows the Gemini wire format, so every AI
 * feature (grammar explanation, writing correction, conversation practice)
 * shares one call/error-handling path instead of duplicating HTTP logic -
 * the same "one place" principle StudySessionService established in Phase 6.
 * AiService's business logic is unchanged from the previous Anthropic-backed
 * version; only this class and its wire format differ.
 *
 * Deliberately fails only at call time, never at startup: a server with no
 * GEMINI_API_KEY configured still runs every other feature normally, and
 * only requests to the AI endpoints get a clear 503.
 */
@Slf4j
@Component
public class GeminiClient {

    private static final String API_URL_TEMPLATE =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    /** App-wide role naming (matches the existing "user"/"assistant" convention used
     * by ConversationMessageDto) - translated to Gemini's "user"/"model" at the wire boundary. */
    private static final String ASSISTANT_ROLE = "assistant";
    private static final String GEMINI_MODEL_ROLE = "model";

    private final AiProperties properties;
    private final RestTemplate restTemplate;

    public GeminiClient(AiProperties properties, RestTemplateBuilder builder) {
        this.properties = properties;
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    /**
     * Sends a (possibly multi-turn) conversation to Gemini and returns the
     * model's reply text. {@code system} may be null/blank for no system
     * instruction. The first entry of {@code messages} must have role "user".
     */
    public String complete(String system, List<Message> messages) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new AiServiceException(
                    "AI features are not configured on this server (missing GEMINI_API_KEY)");
        }
        if (CollectionUtils.isEmpty(messages)) {
            throw new AiServiceException("No conversation content to send to the AI service");
        }

        SystemInstruction systemInstruction = StringUtils.hasText(system)
                ? new SystemInstruction(List.of(new Part(system)))
                : null;
        List<Content> contents = messages.stream()
                .map(m -> new Content(toGeminiRole(m.role()), List.of(new Part(m.content()))))
                .collect(Collectors.toList());
        Request request = new Request(systemInstruction, contents,
                new GenerationConfig(properties.getMaxTokens()));

        String url = API_URL_TEMPLATE.formatted(properties.getModel(), properties.getApiKey());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Response response;
        try {
            response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(request, headers), Response.class
            ).getBody();
        } catch (RestClientException ex) {
            log.error("Gemini API call failed: {}", ex.getMessage());
            throw new AiServiceException("The AI service is currently unavailable. Please try again later.");
        }

        if (response == null || CollectionUtils.isEmpty(response.candidates())) {
            throw new AiServiceException("The AI service returned an empty response");
        }
        Content content = response.candidates().get(0).content();
        if (content == null || CollectionUtils.isEmpty(content.parts())) {
            throw new AiServiceException("The AI service returned no text content");
        }
        return content.parts().stream()
                .map(Part::text)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining());
    }

    private static String toGeminiRole(String appRole) {
        return ASSISTANT_ROLE.equals(appRole) ? GEMINI_MODEL_ROLE : appRole;
    }

    public record Message(String role, String content) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record Request(SystemInstruction systemInstruction, List<Content> contents,
                            GenerationConfig generationConfig) {
    }

    private record SystemInstruction(List<Part> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Content(String role, List<Part> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Part(String text) {
    }

    private record GenerationConfig(@JsonProperty("maxOutputTokens") int maxOutputTokens) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Response(List<Candidate> candidates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Candidate(Content content) {
    }
}
