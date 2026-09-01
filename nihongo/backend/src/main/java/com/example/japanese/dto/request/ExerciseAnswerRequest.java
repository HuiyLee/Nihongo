package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseAnswerRequest {

    @NotBlank(message = "answerText is required")
    private String answerText;

    /** Backs the spec's "isCorrect" field (section 14.3) - never returned to a learner, only accepted from admins. */
    @NotNull(message = "correct is required")
    private Boolean correct;

    @NotNull(message = "orderIndex is required")
    private Integer orderIndex;
}
