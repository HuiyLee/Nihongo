package com.example.japanese.dto.response;

import com.example.japanese.entity.ExamAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Shared by the return value of submit() and GET /api/exams/{id}/result.
 * score/correctCount/wrongCount are all computed on the backend (BR-010);
 * for an EXPIRED attempt they stay at their zero defaults since it was
 * never graded (BR-009).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultResponse {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private ExamAttemptStatus status;
    private int score;
    private int correctCount;
    private int wrongCount;
    private int totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
}
