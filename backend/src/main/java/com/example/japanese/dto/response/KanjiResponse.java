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
public class KanjiResponse {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private Long levelId;
    private String levelCode;
    private String character;
    private String meaning;
    private String onyomi;
    private String kunyomi;
    private Integer strokeCount;
    private String strokeOrderImageUrl;
    private String example;
    private String exampleMeaning;
    private String audioUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
