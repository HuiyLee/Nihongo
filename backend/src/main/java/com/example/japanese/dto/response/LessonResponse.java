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
public class LessonResponse {
    private Long id;
    private Long levelId;
    private String levelCode;
    private String title;
    private String description;
    private String thumbnailUrl;
    private Integer orderIndex;
    private ContentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
