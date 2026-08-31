package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.KanjiRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.KanjiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.service.KanjiService;
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
@RequestMapping("/api/admin/kanji")
@RequiredArgsConstructor
public class AdminKanjiController {

    private final KanjiService kanjiService;

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

    @PostMapping
    public ResponseEntity<ApiResponse<KanjiResponse>> create(@Valid @RequestBody KanjiRequest request) {
        KanjiResponse response = kanjiService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Kanji created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<KanjiResponse> update(@PathVariable Long id, @Valid @RequestBody KanjiRequest request) {
        return ApiResponse.success("Kanji updated", kanjiService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        kanjiService.delete(id);
        return ApiResponse.success("Kanji deleted", null);
    }
}
