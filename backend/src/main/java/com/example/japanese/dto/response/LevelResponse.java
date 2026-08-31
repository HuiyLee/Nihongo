package com.example.japanese.dto.response;

import com.example.japanese.entity.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer orderIndex;
    private ContentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
