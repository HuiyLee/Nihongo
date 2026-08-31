package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.LessonRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.LessonResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.service.LessonService;
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
@RequestMapping("/api/admin/lessons")
@RequiredArgsConstructor
public class AdminLessonController {

    private final LessonService lessonService;

    @GetMapping
    public ApiResponse<PageResponse<LessonResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                lessonService.searchForAdmin(keyword, levelId, status, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<LessonResponse> get(@PathVariable Long id) {
        return ApiResponse.success(lessonService.findByIdForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LessonResponse>> create(@Valid @RequestBody LessonRequest request) {
        LessonResponse response = lessonService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Lesson created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<LessonResponse> update(@PathVariable Long id, @Valid @RequestBody LessonRequest request) {
        return ApiResponse.success("Lesson updated", lessonService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        lessonService.delete(id);
        return ApiResponse.success("Lesson deleted", null);
    }
}
