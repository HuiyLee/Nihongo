package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Admin-only view of an answer option - includes isCorrect so admins can review/edit the correct choice(s). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminExerciseAnswerResponse {
    private Long id;
    private String answerText;
    private boolean correct;
    private int orderIndex;
}
