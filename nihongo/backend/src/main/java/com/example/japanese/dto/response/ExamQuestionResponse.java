package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Public/learner view of one exam question - nests the masked ExerciseResponse (no isCorrect). */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionResponse {
    private Long id;
    private int orderIndex;
    private ExerciseResponse exercise;
}
