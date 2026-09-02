package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * One question's full review ("đáp án" - answer key) for a concluded exam
 * attempt. Only ever populated on ExamResultResponse for a COMPLETED or
 * EXPIRED attempt (see ExamService.submit/result) - never while an attempt
 * is still IN_PROGRESS, which is what keeps ExamQuestionResponse (used by
 * start()) answer-blind during the exam itself so a learner can't peek at
 * the correct answer before submitting.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionReviewResponse {
    private Long examQuestionId;
    private int orderIndex;
    /** isCorrect-bearing view of the question + its answer options (same shape the admin editor uses). */
    private AdminExerciseResponse exercise;
    /** Answer IDs the learner actually picked for this question - empty if they left it blank. */
    private List<Long> selectedAnswerIds;
    private boolean correct;
}
