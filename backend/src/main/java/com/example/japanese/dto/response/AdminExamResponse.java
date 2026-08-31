package com.example.japanese.dto.response;

import com.example.japanese.entity.ContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/** Admin view of an exam - nests every question with its full AdminExerciseResponse (isCorrect included) for editing. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminExamResponse {
    private Long id;
    private Long levelId;
    private String levelCode;
    private String title;
    private String description;
    private int durationMinutes;
    private int totalQuestions;
    private ContentStatus status;
    private List<AdminExamQuestionResponse> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
