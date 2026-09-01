package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KanjiRequest {

    private Long lessonId;

    @NotNull(message = "levelId is required")
    private Long levelId;

    @NotBlank(message = "character is required")
    private String character;

    @NotBlank(message = "meaning is required")
    private String meaning;

    private String onyomi;
    private String kunyomi;

    @Positive(message = "strokeCount must be positive")
    private Integer strokeCount;

    private String strokeOrderImageUrl;
    private String example;
    private String exampleMeaning;
    private String audioUrl;
}
