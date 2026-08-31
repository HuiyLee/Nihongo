package com.example.japanese.dto.request;

import com.example.japanese.entity.ExerciseDifficulty;
import com.example.japanese.entity.ExerciseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExerciseRequest {

    private Long lessonId;

    @NotNull(message = "levelId is required")
    private Long levelId;

    @NotNull(message = "type is required")
    private ExerciseType type;

    @NotBlank(message = "question is required")
    private String question;

    private String explanation;
    private String audioUrl;
    private String imageUrl;

    @NotNull(message = "difficulty is required")
    private ExerciseDifficulty difficulty;

    @NotEmpty(message = "At least one answer is required")
    @Valid
    private List<ExerciseAnswerRequest> answers;
}
