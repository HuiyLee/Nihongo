package com.example.japanese.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Requirements section 38, Phase 7 (optional AI features). Backs GeminiClient
 * (Google's free-tier Gemini API).
 * apiKey is intentionally allowed to be blank - the app must still start and
 * every other feature must keep working without it (GeminiClient fails
 * only at call time, with a clear error, never at startup).
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {

    private String apiKey;

    private String model;

    private int maxTokens;
}
