package com.example.japanese.mapper;

import com.example.japanese.dto.response.KanjiResponse;
import com.example.japanese.entity.Kanji;
import org.springframework.stereotype.Component;

@Component
public class KanjiMapper {

    public KanjiResponse toResponse(Kanji kanji) {
        if (kanji == null) {
            return null;
        }
        return KanjiResponse.builder()
                .id(kanji.getId())
                .lessonId(kanji.getLesson() != null ? kanji.getLesson().getId() : null)
                .lessonTitle(kanji.getLesson() != null ? kanji.getLesson().getTitle() : null)
                .levelId(kanji.getLevel().getId())
                .levelCode(kanji.getLevel().getCode())
                .character(kanji.getCharacter())
                .meaning(kanji.getMeaning())
                .onyomi(kanji.getOnyomi())
                .kunyomi(kanji.getKunyomi())
                .strokeCount(kanji.getStrokeCount())
                .strokeOrderImageUrl(kanji.getStrokeOrderImageUrl())
                .example(kanji.getExample())
                .exampleMeaning(kanji.getExampleMeaning())
                .audioUrl(kanji.getAudioUrl())
                .createdAt(kanji.getCreatedAt())
                .updatedAt(kanji.getUpdatedAt())
                .build();
    }
}
