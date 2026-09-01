package com.example.japanese.controller;

import com.example.japanese.dto.request.SubmitExamRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.ExamAttemptResponse;
import com.example.japanese.dto.response.ExamResponse;
import com.example.japanese.dto.response.ExamResultResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.Role;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.ExamService;
import com.example.japanese.util.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Requirements section 17-18. List/get reuse the flat, question-free ExamResponse
 * for everyone (admins included, so previewing a draft here never risks leaking
 * isCorrect - that DTO simply never nests a question). Admins see every status;
 * everyone else only ever sees PUBLISHED exams (BR-006/007/008).
 *
 * start/submit/result are always scoped to the authenticated caller - the userId
 * never comes from the client.
 */
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping
    public ApiResponse<PageResponse<ExamResponse>> list(
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
            return ApiResponse.success(examService.searchAllForBrowse(keyword, levelId, status, pageable));
        }
        return ApiResponse.success(examService.searchPublished(keyword, levelId, pageable));
    }

    @GetMapping("/{id}")
    public ApiResponse<ExamResponse> get(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        if (isAdmin(principal)) {
            return ApiResponse.success(examService.findAnyByIdForBrowse(id));
        }
        return ApiResponse.success(examService.findPublishedById(id));
    }

    @PostMapping("/{id}/start")
    public ApiResponse<ExamAttemptResponse> start(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ApiResponse.success(examService.start(principal.getId(), id));
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<ExamResultResponse> submit(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody SubmitExamRequest request
    ) {
        return ApiResponse.success("Exam submitted", examService.submit(principal.getId(), id, request));
    }

    @GetMapping("/{id}/result")
    public ApiResponse<ExamResultResponse> result(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return ApiResponse.success(examService.result(principal.getId(), id));
    }

    /** Requirements section 38 Phase 5 ("Auto save") - called periodically while an attempt is in progress. */
    @PutMapping("/{id}/save")
    public ApiResponse<Void> saveProgress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody SubmitExamRequest request
    ) {
        examService.saveProgress(principal.getId(), id, request);
        return ApiResponse.success("Progress saved", null);
    }

    /**
     * Requirements section 38 Phase 5 ("History"). A fixed literal path
     * ("attempts/history"), not a variable one, so it can never collide
     * with GET /{id} above regardless of routing order.
     */
    @GetMapping("/attempts/history")
    public ApiResponse<PageResponse<ExamResultResponse>> history(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(examService.history(principal.getId(), PageableFactory.build(page, size, sort)));
    }

    private boolean isAdmin(UserPrincipal principal) {
        return principal != null && Role.ADMIN.equals(principal.getRole());
    }
}
