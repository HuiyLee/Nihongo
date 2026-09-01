package com.example.japanese.controller;

import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.StudySessionResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.StudySessionService;
import com.example.japanese.util.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Requirements section 21. Always scoped to the caller - never accepts a userId from the client. */
@RestController
@RequestMapping("/api/study-sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService studySessionService;

    @PostMapping
    public ApiResponse<StudySessionResponse> record(
            @AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody StudySessionRequest request
    ) {
        return ApiResponse.success(studySessionService.record(principal.getId(), request));
    }

    @GetMapping
    public ApiResponse<PageResponse<StudySessionResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                studySessionService.list(principal.getId(), PageableFactory.build(page, size, sort))
        );
    }
}
