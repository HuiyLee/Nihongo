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
public class GrammarResponse {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private Long levelId;
    private String levelCode;
    private String pattern;
    private String meaning;
    private String formation;
    private String explanation;
    private String example;
    private String exampleMeaning;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
