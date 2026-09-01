package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.ExerciseRequest;
import com.example.japanese.dto.response.AdminExerciseResponse;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.ExerciseType;
import com.example.japanese.service.ExerciseService;
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

/** Requirements section 14. Admin responses include isCorrect on every answer - see AdminExerciseAnswerResponse. */
@RestController
@RequestMapping("/api/admin/exercises")
@RequiredArgsConstructor
public class AdminExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ApiResponse<PageResponse<AdminExerciseResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) Long readingId,
            @RequestParam(required = false) ExerciseType type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                exerciseService.searchForAdmin(keyword, levelId, lessonId, readingId, type, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminExerciseResponse> get(@PathVariable Long id) {
        return ApiResponse.success(exerciseService.findByIdForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminExerciseResponse>> create(@Valid @RequestBody ExerciseRequest request) {
        AdminExerciseResponse response = exerciseService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Exercise created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminExerciseResponse> update(@PathVariable Long id, @Valid @RequestBody ExerciseRequest request) {
        return ApiResponse.success("Exercise updated", exerciseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        exerciseService.delete(id);
        return ApiResponse.success("Exercise deleted", null);
    }
}
