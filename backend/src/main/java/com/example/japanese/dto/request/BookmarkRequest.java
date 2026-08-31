package com.example.japanese.dto.request;

import com.example.japanese.entity.BookmarkTargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkRequest {

    @NotNull(message = "targetType is required")
    private BookmarkTargetType targetType;

    @NotNull(message = "targetId is required")
    private Long targetId;
}
