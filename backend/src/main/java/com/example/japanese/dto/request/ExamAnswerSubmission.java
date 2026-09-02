package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * One question's submitted selection within a SubmitExamRequest.
 *
 * answerIds is intentionally allowed to be null/empty: the frontend submits
 * one entry per question in the exam (see ExamAttemptPage.tsx), including
 * ones the learner hasn't answered yet, so a not-yet-answered question is a
 * completely normal submission, not a validation error - ExamService already
 * treats a missing/empty answer set as "no answer" (Set.of() fallback) when
 * grading. Requiring a non-empty list here used to reject the whole request
 * (auto-save, and any submit before every question was answered) with one
 * "answerIds is required" error per unanswered question.
 */
@Getter
@Setter
public class ExamAnswerSubmission {

    @NotNull(message = "examQuestionId is required")
    private Long examQuestionId;

    private List<Long> answerIds;
}
