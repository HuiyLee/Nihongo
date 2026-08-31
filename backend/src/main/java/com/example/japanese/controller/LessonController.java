package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.LessonResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.Role;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.LessonService;
import com.example.japanese.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * User-facing lesson browsing. Admins hitting this endpoint still see every
 * status (so the UI can be reused to preview drafts); everyone else only
 * ever sees PUBLISHED lessons no matter what status they ask for (BR-006/007).
 */
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping
    public ApiResponse<PageResponse<LessonResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        var pageable = PageableFactory.build(page, size, sort);
        if (isAdmin(principal)) {
            return ApiResponse.success(lessonService.searchForAdmin(keyword, levelId, status, pageable));
        }
        return ApiResponse.success(lessonService.searchPublished(keyword, levelId, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<LessonResponse> get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        if (isAdmin(principal)) {
            return ApiResponse.success(lessonService.findByIdForAdmin(id));
        }
        return ApiResponse.success(lessonService.findPublishedById(id));
    }

    private boolean isAdmin(UserPrincipal principal) {
        return principal != null && Role.ADMIN.equals(principal.getRole());
    }
}
