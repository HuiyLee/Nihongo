package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.StreakResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.StreakService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Requirements section 22. Always scoped to the caller. */
@RestController
@RequestMapping("/api/streak")
@RequiredArgsConstructor
public class StreakController {

    private final StreakService streakService;

    @GetMapping
    public ApiResponse<StreakResponse> get(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(streakService.getStreak(principal.getId()));
    }
}
