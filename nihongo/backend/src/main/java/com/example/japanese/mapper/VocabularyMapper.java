package com.example.japanese.mapper;

import com.example.japanese.dto.response.VocabularyResponse;
import com.example.japanese.entity.Vocabulary;
import org.springframework.stereotype.Component;

@Component
public class VocabularyMapper {

    public VocabularyResponse toResponse(Vocabulary vocabulary) {
        if (vocabulary == null) {
            return null;
        }
        return VocabularyResponse.builder()
                .id(vocabulary.getId())
                .lessonId(vocabulary.getLesson() != null ? vocabulary.getLesson().getId() : null)
                .lessonTitle(vocabulary.getLesson() != null ? vocabulary.getLesson().getTitle() : null)
                .levelId(vocabulary.getLevel().getId())
                .levelCode(vocabulary.getLevel().getCode())
                .word(vocabulary.getWord())
                .kanji(vocabulary.getKanji())
                .hiragana(vocabulary.getHiragana())
                .katakana(vocabulary.getKatakana())
                .romaji(vocabulary.getRomaji())
                .meaning(vocabulary.getMeaning())
                .partOfSpeech(vocabulary.getPartOfSpeech())
                .example(vocabulary.getExample())
                .exampleMeaning(vocabulary.getExampleMeaning())
                .audioUrl(vocabulary.getAudioUrl())
                .imageUrl(vocabulary.getImageUrl())
                .createdAt(vocabulary.getCreatedAt())
                .updatedAt(vocabulary.getUpdatedAt())
                .build();
    }
}
