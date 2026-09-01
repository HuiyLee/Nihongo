package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VocabularyRequest {

    private Long lessonId;

    @NotNull(message = "levelId is required")
    private Long levelId;

    @NotBlank(message = "word is required")
    private String word;

    private String kanji;
    private String hiragana;
    private String katakana;
    private String romaji;

    @NotBlank(message = "meaning is required")
    private String meaning;

    private String partOfSpeech;
    private String example;
    private String exampleMeaning;
    private String audioUrl;
    private String imageUrl;
}
