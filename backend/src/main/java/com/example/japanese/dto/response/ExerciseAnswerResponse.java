package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Public/learner view of an answer option - deliberately omits isCorrect (see AdminExerciseAnswerResponse). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseAnswerResponse {
    private Long id;
    private String answerText;
    private int orderIndex;
}
