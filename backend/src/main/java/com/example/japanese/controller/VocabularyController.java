package com.example.japanese.controller;

import com.example.japanese.dto.request.MarkStatusRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.VocabularyResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.UserVocabularyService;
import com.example.japanese.service.VocabularyService;
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

/** Requirements section 9.2 - search by word/kanji/hiragana/katakana/romaji/meaning, filter by level/lesson. */
@RestController
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;
    private final UserVocabularyService userVocabularyService;

    @GetMapping
    public ApiResponse<PageResponse<VocabularyResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                vocabularyService.search(keyword, levelId, lessonId, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<VocabularyResponse> get(@PathVariable Long id) {
        return ApiResponse.success(vocabularyService.findById(id));
    }

    /** The caller's own learning state for this item - never another user's (section 33). */
    @GetMapping("/{id}/progress")
    public ApiResponse<LearningProgressResponse> getProgress(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id
    ) {
        return ApiResponse.success(userVocabularyService.getProgress(principal.getId(), id));
    }

    @PostMapping("/{id}/mark")
    public ApiResponse<LearningProgressResponse> mark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody MarkStatusRequest request
    ) {
        return ApiResponse.success(userVocabularyService.mark(principal.getId(), id, request.getOutcome()));
    }
}
