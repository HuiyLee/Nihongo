package com.example.japanese.controller;

import com.example.japanese.dto.request.SubmitExerciseRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.ExerciseResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.SubmitExerciseResponse;
import com.example.japanese.entity.ExerciseType;
import com.example.japanese.service.ExerciseService;
import com.example.japanese.util.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Requirements section 14. Every response here is the masked ExerciseResponse -
 * isCorrect never reaches a learner before they submit (section 14.4).
 */
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ApiResponse<PageResponse<ExerciseResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Long lessonId,
            @RequestParam(required = false) ExerciseType type,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                exerciseService.search(keyword, levelId, lessonId, type, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ExerciseResponse> get(@PathVariable Long id) {
        return ApiResponse.success(exerciseService.findById(id));
    }

    @PostMapping("/{id}/submit")
    public ApiResponse<SubmitExerciseResponse> submit(
            @PathVariable Long id, @Valid @RequestBody SubmitExerciseRequest request
    ) {
        return ApiResponse.success("Exercise submitted", exerciseService.submit(id, request));
    }
}
