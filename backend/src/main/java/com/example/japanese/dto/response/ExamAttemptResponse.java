package com.example.japanese.dto.response;

import com.example.japanese.entity.ExamAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Returned by POST /api/exams/{id}/start - this is what delivers the
 * question list to the client (section 18: "Create ExamAttempt" is
 * immediately followed by "Display questions"). The frontend computes its
 * own countdown from startedAt + durationMinutes; the backend never trusts
 * that countdown back (section 18: "backend must not trust the frontend timer").
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptResponse {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private int durationMinutes;
    private LocalDateTime startedAt;
    private ExamAttemptStatus status;
    private List<ExamQuestionResponse> questions;
}
