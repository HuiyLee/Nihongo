package com.example.japanese.util;

import java.security.SecureRandom;
import java.util.Base64;

/** Generates opaque, unguessable refresh-token strings (kept separate from JWT signing). */
public final class TokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private TokenGenerator() {
    }

    public static String generate() {
        byte[] bytes = new byte[64];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
