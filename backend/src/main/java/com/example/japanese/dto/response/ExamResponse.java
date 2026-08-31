package com.example.japanese.dto.response;

import com.example.japanese.entity.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Public/learner view of an exam - the flat field list from section 17.1
 * only. Question content is deliberately not nested here (a learner
 * shouldn't be able to browse every question before starting); it's
 * delivered by ExamAttemptResponse once they actually start the exam.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {
    private Long id;
    private Long levelId;
    private String levelCode;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalQuestions;
    private ContentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
