package com.example.japanese.mapper;

import com.example.japanese.dto.response.ReadingResponse;
import com.example.japanese.entity.Reading;
import org.springframework.stereotype.Component;

@Component
public class ReadingMapper {

    /**
     * @param revealTranslation whether the translation field should be included.
     * @param completed         completion flag to attach, or null when not applicable (admin views).
     */
    public ReadingResponse toResponse(Reading reading, boolean revealTranslation, Boolean completed) {
        if (reading == null) {
            return null;
        }
        return ReadingResponse.builder()
                .id(reading.getId())
                .levelId(reading.getLevel().getId())
                .levelCode(reading.getLevel().getCode())
                .title(reading.getTitle())
                .content(reading.getContent())
                .translation(revealTranslation ? reading.getTranslation() : null)
                .difficulty(reading.getDifficulty())
                .completed(completed)
                .createdAt(reading.getCreatedAt())
                .updatedAt(reading.getUpdatedAt())
                .build();
    }
}
