package com.example.japanese.dto.response;

import com.example.japanese.entity.BookmarkTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private Long id;
    private BookmarkTargetType targetType;
    private Long targetId;

    /** Short human-readable label for the bookmarked item (word/character/pattern), resolved at read time. */
    private String displayText;

    private LocalDateTime createdAt;
}
