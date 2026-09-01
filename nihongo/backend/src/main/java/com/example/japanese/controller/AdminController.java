package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Placeholder admin endpoint for Phase 1. Real admin CRUD (users, levels,
 * lessons, vocabulary, ...) lands in Phase 2 - kept here only so the
 * ROLE_ADMIN authorization rule in SecurityConfig has something to guard.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/ping")
    public ApiResponse<Map<String, String>> ping() {
        return ApiResponse.success(Map.of("message", "pong from admin-only endpoint"));
    }
}
