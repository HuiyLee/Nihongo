package com.example.japanese.controller;

import com.example.japanese.dto.request.MarkStatusRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.GrammarResponse;
import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.GrammarService;
import com.example.japanese.service.UserGrammarService;
import com.example.japanese.util.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Requirements section 13.3. */
@RestController
@RequestMapping("/api/grammars")
@RequiredArgsConstructor
public class GrammarController {

    private final GrammarService grammarService;
    private final UserGrammarService userGrammarService;

    @GetMapping
    public ApiResponse<PageResponse<GrammarResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                grammarService.search(keyword, levelId, lessonId, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<GrammarResponse> get(@PathVariable Long id) {
        return ApiResponse.success(grammarService.findById(id));
    }

    @GetMapping("/{id}/progress")
    public ApiResponse<LearningProgressResponse> getProgress(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id
    ) {
        return ApiResponse.success(userGrammarService.getProgress(principal.getId(), id));
    }

    @PostMapping("/{id}/mark")
    public ApiResponse<LearningProgressResponse> mark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody MarkStatusRequest request
    ) {
        return ApiResponse.success(userGrammarService.mark(principal.getId(), id, request.getOutcome()));
    }
}
