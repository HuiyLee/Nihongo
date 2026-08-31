package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminExamQuestionResponse {
    private Long id;
    private int orderIndex;
    private AdminExerciseResponse exercise;
}
