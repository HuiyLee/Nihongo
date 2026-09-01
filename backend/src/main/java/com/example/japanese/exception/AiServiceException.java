package com.example.japanese.exception;

/**
 * Requirements section 38, Phase 7 - thrown when the AI provider is not
 * configured (no API key) or a call to it fails/times out. Mapped to 503
 * SERVICE_UNAVAILABLE by GlobalExceptionHandler, distinct from a 400/404
 * caused by the caller's own request.
 */
public class AiServiceException extends RuntimeException {
    public AiServiceException(String message) {
        super(message);
    }
}
