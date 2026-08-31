package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQuestionRequest {

    @NotNull(message = "exerciseId is required")
    private Long exerciseId;

    @NotNull(message = "orderIndex is required")
    private Integer orderIndex;
}
