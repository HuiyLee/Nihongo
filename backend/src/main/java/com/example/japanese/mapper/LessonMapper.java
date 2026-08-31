package com.example.japanese.mapper;

import com.example.japanese.dto.response.LessonResponse;
import com.example.japanese.entity.Lesson;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        return LessonResponse.builder()
                .id(lesson.getId())
                .levelId(lesson.getLevel().getId())
                .levelCode(lesson.getLevel().getCode())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .thumbnailUrl(lesson.getThumbnailUrl())
                .orderIndex(lesson.getOrderIndex())
                .status(lesson.getStatus())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
