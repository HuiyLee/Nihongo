package com.example.japanese.controller;

import com.example.japanese.dto.request.MarkStatusRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.KanjiResponse;
import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.KanjiService;
import com.example.japanese.service.UserKanjiService;
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

/** Requirements section 12.3. */
@RestController
@RequestMapping("/api/kanji")
@RequiredArgsConstructor
public class KanjiController {

    private final KanjiService kanjiService;
    private final UserKanjiService userKanjiService;

    @GetMapping
    public ApiResponse<PageResponse<KanjiResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                kanjiService.search(keyword, levelId, lessonId, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<KanjiResponse> get(@PathVariable Long id) {
        return ApiResponse.success(kanjiService.findById(id));
    }

    @GetMapping("/{id}/progress")
    public ApiResponse<LearningProgressResponse> getProgress(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id
    ) {
        return ApiResponse.success(userKanjiService.getProgress(principal.getId(), id));
    }

    @PostMapping("/{id}/mark")
    public ApiResponse<LearningProgressResponse> mark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody MarkStatusRequest request
    ) {
        return ApiResponse.success(userKanjiService.mark(principal.getId(), id, request.getOutcome()));
    }
}
