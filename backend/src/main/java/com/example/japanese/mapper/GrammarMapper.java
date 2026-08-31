package com.example.japanese.mapper;

import com.example.japanese.dto.response.GrammarResponse;
import com.example.japanese.entity.Grammar;
import org.springframework.stereotype.Component;

@Component
public class GrammarMapper {

    public GrammarResponse toResponse(Grammar grammar) {
        if (grammar == null) {
            return null;
        }
        return GrammarResponse.builder()
                .id(grammar.getId())
                .lessonId(grammar.getLesson() != null ? grammar.getLesson().getId() : null)
                .lessonTitle(grammar.getLesson() != null ? grammar.getLesson().getTitle() : null)
                .levelId(grammar.getLevel().getId())
                .levelCode(grammar.getLevel().getCode())
                .pattern(grammar.getPattern())
                .meaning(grammar.getMeaning())
                .formation(grammar.getFormation())
                .explanation(grammar.getExplanation())
                .example(grammar.getExample())
                .exampleMeaning(grammar.getExampleMeaning())
                .notes(grammar.getNotes())
                .createdAt(grammar.getCreatedAt())
                .updatedAt(grammar.getUpdatedAt())
                .build();
    }
}
