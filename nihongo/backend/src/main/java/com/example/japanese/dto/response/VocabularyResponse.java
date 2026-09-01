package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyResponse {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private Long levelId;
    private String levelCode;
    private String word;
    private String kanji;
    private String hiragana;
    private String katakana;
    private String romaji;
    private String meaning;
    private String partOfSpeech;
    private String example;
    private String exampleMeaning;
    private String audioUrl;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
