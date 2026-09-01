package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.CategoryProgressResponse;
import com.example.japanese.dto.response.ProgressOverviewResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Requirements section 20. Always scoped to the caller. */
@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping
    public ApiResponse<ProgressOverviewResponse> overview(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(progressService.overview(principal.getId()));
    }

    @GetMapping("/vocabulary")
    public ApiResponse<CategoryProgressResponse> vocabulary(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(progressService.vocabulary(principal.getId()));
    }

    @GetMapping("/kanji")
    public ApiResponse<CategoryProgressResponse> kanji(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(progressService.kanji(principal.getId()));
    }

    @GetMapping("/grammar")
    public ApiResponse<CategoryProgressResponse> grammar(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(progressService.grammar(principal.getId()));
    }

    @GetMapping("/lessons")
    public ApiResponse<CategoryProgressResponse> lessons(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(progressService.lessons(principal.getId()));
    }

    @GetMapping("/exams")
    public ApiResponse<CategoryProgressResponse> exams(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(progressService.exams(principal.getId()));
    }
}
