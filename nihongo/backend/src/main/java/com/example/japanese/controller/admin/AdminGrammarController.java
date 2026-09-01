package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.GrammarRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.GrammarResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.service.GrammarService;
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
@RequestMapping("/api/admin/grammars")
@RequiredArgsConstructor
public class AdminGrammarController {

    private final GrammarService grammarService;

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

    @PostMapping
    public ResponseEntity<ApiResponse<GrammarResponse>> create(@Valid @RequestBody GrammarRequest request) {
        GrammarResponse response = grammarService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Grammar created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<GrammarResponse> update(@PathVariable Long id, @Valid @RequestBody GrammarRequest request) {
        return ApiResponse.success("Grammar updated", grammarService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        grammarService.delete(id);
        return ApiResponse.success("Grammar deleted", null);
    }
}
