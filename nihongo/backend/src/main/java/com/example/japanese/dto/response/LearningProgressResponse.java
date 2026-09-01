package com.example.japanese.dto.response;

import com.example.japanese.entity.LearningStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgressResponse {
    private LearningStatus status;
    private int correctCount;
    private int wrongCount;
    private LocalDateTime lastReviewedAt;
    private LocalDateTime nextReviewAt;
}
