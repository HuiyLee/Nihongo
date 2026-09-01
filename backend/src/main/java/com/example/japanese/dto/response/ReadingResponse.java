package com.example.japanese.dto.response;

import com.example.japanese.entity.ExerciseDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * `translation` is only populated when the caller is allowed to see it -
 * admins always, and regular users only once they've completed the passage
 * (ReadingService decides this; the mapper itself never masks anything).
 * `completed` is null in admin contexts where "completion" doesn't apply.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingResponse {
    private Long id;
    private Long levelId;
    private String levelCode;
    private String title;
    private String content;
    private String translation;
    private ExerciseDifficulty difficulty;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
