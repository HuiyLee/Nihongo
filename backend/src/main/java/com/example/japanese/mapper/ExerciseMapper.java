package com.example.japanese.mapper;

import com.example.japanese.dto.response.AdminExerciseAnswerResponse;
import com.example.japanese.dto.response.AdminExerciseResponse;
import com.example.japanese.dto.response.ExerciseAnswerResponse;
import com.example.japanese.dto.response.ExerciseResponse;
import com.example.japanese.entity.Exercise;
import com.example.japanese.entity.ExerciseAnswer;
import org.springframework.stereotype.Component;

/** Two response shapes on purpose - the admin one is the only place isCorrect is ever serialized. */
@Component
public class ExerciseMapper {

    public ExerciseResponse toResponse(Exercise exercise) {
        if (exercise == null) {
            return null;
        }
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .lessonId(exercise.getLesson() != null ? exercise.getLesson().getId() : null)
                .lessonTitle(exercise.getLesson() != null ? exercise.getLesson().getTitle() : null)
                .levelId(exercise.getLevel().getId())
                .levelCode(exercise.getLevel().getCode())
                .type(exercise.getType())
                .question(exercise.getQuestion())
                .explanation(exercise.getExplanation())
                .audioUrl(exercise.getAudioUrl())
                .imageUrl(exercise.getImageUrl())
                .difficulty(exercise.getDifficulty())
                .answers(exercise.getAnswers().stream().map(this::toAnswerResponse).toList())
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .build();
    }

    public AdminExerciseResponse toAdminResponse(Exercise exercise) {
        if (exercise == null) {
            return null;
        }
        return AdminExerciseResponse.builder()
                .id(exercise.getId())
                .lessonId(exercise.getLesson() != null ? exercise.getLesson().getId() : null)
                .lessonTitle(exercise.getLesson() != null ? exercise.getLesson().getTitle() : null)
                .levelId(exercise.getLevel().getId())
                .levelCode(exercise.getLevel().getCode())
                .type(exercise.getType())
                .question(exercise.getQuestion())
                .explanation(exercise.getExplanation())
                .audioUrl(exercise.getAudioUrl())
                .imageUrl(exercise.getImageUrl())
                .difficulty(exercise.getDifficulty())
                .answers(exercise.getAnswers().stream().map(this::toAdminAnswerResponse).toList())
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .build();
    }

    private ExerciseAnswerResponse toAnswerResponse(ExerciseAnswer answer) {
        return ExerciseAnswerResponse.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .orderIndex(answer.getOrderIndex())
                .build();
    }

    private AdminExerciseAnswerResponse toAdminAnswerResponse(ExerciseAnswer answer) {
        return AdminExerciseAnswerResponse.builder()
                .id(answer.getId())
                .answerText(answer.getAnswerText())
                .correct(answer.isCorrect())
                .orderIndex(answer.getOrderIndex())
                .build();
    }
}
