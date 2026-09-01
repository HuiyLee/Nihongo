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
 *
 * savedAnswers carries whatever was last written by PUT /{id}/save (or by a
 * previous submit, defensively) - empty on a brand-new attempt, populated
 * when start() resumes a live one, so the client can restore selections
 * after a refresh instead of losing progress (section 38 Phase 5: "Auto save").
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
    private List<SavedAnswerResponse> savedAnswers;
}
