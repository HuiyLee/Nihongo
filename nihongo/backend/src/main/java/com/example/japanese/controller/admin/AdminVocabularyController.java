package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.VocabularyRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.VocabularyResponse;
import com.example.japanese.service.VocabularyService;
import com.example.japanese.util.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/vocabularies")
@RequiredArgsConstructor
public class AdminVocabularyController {

    private final VocabularyService vocabularyService;

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

    @PostMapping
    public ResponseEntity<ApiResponse<VocabularyResponse>> create(@Valid @RequestBody VocabularyRequest request) {
        VocabularyResponse response = vocabularyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Vocabulary created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<VocabularyResponse> update(@PathVariable Long id, @Valid @RequestBody VocabularyRequest request) {
        return ApiResponse.success("Vocabulary updated", vocabularyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        vocabularyService.delete(id);
        return ApiResponse.success("Vocabulary deleted", null);
    }
}
