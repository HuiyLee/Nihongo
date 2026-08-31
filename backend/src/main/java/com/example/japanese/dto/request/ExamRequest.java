package com.example.japanese.dto.request;

import com.example.japanese.entity.ContentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** Note: totalQuestions is intentionally absent - the service always computes it from questions.size(). */
@Getter
@Setter
public class ExamRequest {

    @NotNull(message = "levelId is required")
    private Long levelId;

    @NotBlank(message = "title is required")
    private String title;

    private String description;

    @NotNull(message = "durationMinutes is required")
    @Positive(message = "durationMinutes must be positive")
    private Integer durationMinutes;

    @NotNull(message = "status is required")
    private ContentStatus status;

    @NotEmpty(message = "At least one question is required")
    @Valid
    private List<ExamQuestionRequest> questions;
}
