package com.example.japanese.mapper;

import com.example.japanese.dto.response.LevelResponse;
import com.example.japanese.entity.Level;
import org.springframework.stereotype.Component;

@Component
public class LevelMapper {

    public LevelResponse toResponse(Level level) {
        if (level == null) {
            return null;
        }
        return LevelResponse.builder()
                .id(level.getId())
                .code(level.getCode())
                .name(level.getName())
                .description(level.getDescription())
                .orderIndex(level.getOrderIndex())
                .status(level.getStatus())
                .createdAt(level.getCreatedAt())
                .updatedAt(level.getUpdatedAt())
                .build();
    }
}
