package com.example.japanese.dto.response;

import com.example.japanese.entity.ExerciseDifficulty;
import com.example.japanese.entity.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** Admin view of an exercise - includes isCorrect on every answer so the admin can review/edit it. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminExerciseResponse {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private Long levelId;
    private String levelCode;
    private ExerciseType type;
    private String question;
    private String explanation;
    private String audioUrl;
    private String imageUrl;
    private ExerciseDifficulty difficulty;
    private List<AdminExerciseAnswerResponse> answers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
