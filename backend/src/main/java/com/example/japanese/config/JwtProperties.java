package com.example.japanese.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /** Base64-encoded HMAC-SHA256 secret. */
    private String secret;

    private long accessTokenExpirationMs;

    private long refreshTokenExpirationMs;
}
