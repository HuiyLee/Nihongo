package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrammarRequest {

    private Long lessonId;

    @NotNull(message = "levelId is required")
    private Long levelId;

    @NotBlank(message = "pattern is required")
    private String pattern;

    @NotBlank(message = "meaning is required")
    private String meaning;

    private String formation;
    private String explanation;
    private String example;
    private String exampleMeaning;
    private String notes;
}
