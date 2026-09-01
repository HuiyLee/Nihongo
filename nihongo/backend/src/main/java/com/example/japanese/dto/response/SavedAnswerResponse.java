package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * One question's previously auto-saved selection. Returned inside
 * ExamAttemptResponse when start() resumes a live attempt, so the client
 * can pre-fill the form instead of losing progress on a page refresh.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedAnswerResponse {
    private Long examQuestionId;
    private List<Long> answerIds;
}
