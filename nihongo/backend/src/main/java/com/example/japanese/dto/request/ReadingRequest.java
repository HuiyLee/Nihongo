package com.example.japanese.dto.request;

import com.example.japanese.entity.ExerciseDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingRequest {

    @NotNull(message = "levelId is required")
    private Long levelId;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "content is required")
    private String content;

    private String translation;

    @NotNull(message = "difficulty is required")
    private ExerciseDifficulty difficulty;
}
