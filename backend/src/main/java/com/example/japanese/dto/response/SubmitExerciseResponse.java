package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Requirements section 14.4 - {"correct": ..., "score": ..., "explanation": ...}. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitExerciseResponse {
    private boolean correct;
    private int score;
    private String explanation;
}
