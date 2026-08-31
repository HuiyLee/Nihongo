package com.example.japanese.dto.request;

import com.example.japanese.entity.ContentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonRequest {

    @NotNull(message = "levelId is required")
    private Long levelId;

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    private String description;

    private String thumbnailUrl;

    @NotNull(message = "orderIndex is required")
    private Integer orderIndex;

    @NotNull(message = "status is required")
    private ContentStatus status;
}
