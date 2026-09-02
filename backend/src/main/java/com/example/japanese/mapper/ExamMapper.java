package com.example.japanese.mapper;

import com.example.japanese.dto.response.AdminExamQuestionResponse;
import com.example.japanese.dto.response.AdminExamResponse;
import com.example.japanese.dto.response.ExamQuestionResponse;
import com.example.japanese.dto.response.ExamQuestionReviewResponse;
import com.example.japanese.dto.response.ExamResponse;
import com.example.japanese.entity.Exam;
import com.example.japanese.entity.ExamQuestion;
import com.example.japanese.entity.ExerciseAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/** Reuses ExerciseMapper for each question's nested exercise, so the isCorrect-masking logic exists in exactly one place. */
@Component
@RequiredArgsConstructor
public class ExamMapper {

    private final ExerciseMapper exerciseMapper;

    /** Public/learner view - flat fields only, no nested questions (see ExamResponse). */
    public ExamResponse toResponse(Exam exam) {
        if (exam == null) {
            return null;
        }
        return ExamResponse.builder()
                .id(exam.getId())
                .levelId(exam.getLevel().getId())
                .levelCode(exam.getLevel().getCode())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .status(exam.getStatus())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }

    public AdminExamResponse toAdminResponse(Exam exam) {
        if (exam == null) {
            return null;
        }
        return AdminExamResponse.builder()
                .id(exam.getId())
                .levelId(exam.getLevel().getId())
                .levelCode(exam.getLevel().getCode())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .status(exam.getStatus())
                .questions(exam.getQuestions().stream().map(this::toAdminQuestionResponse).toList())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }

    /** Masked view of one question, used inside ExamAttemptResponse once a learner starts the exam. */
    public ExamQuestionResponse toQuestionResponse(ExamQuestion question) {
        return ExamQuestionResponse.builder()
                .id(question.getId())
                .orderIndex(question.getOrderIndex())
                .exercise(exerciseMapper.toResponse(question.getExercise()))
                .build();
    }

    /**
     * Full answer-key view of one question in a concluded exam attempt -
     * reuses the admin (isCorrect-bearing) exercise mapping, since revealing
     * the correct answer is exactly as sensitive as the admin editor's view
     * and is only ever called for a COMPLETED/EXPIRED attempt (see
     * ExamService.toResultResponse), never while the exam is still in progress.
     */
    public ExamQuestionReviewResponse toReviewQuestionResponse(ExamQuestion question, Set<Long> selectedAnswerIds) {
        Set<Long> correctAnswerIds = question.getExercise().getAnswers().stream()
                .filter(ExerciseAnswer::isCorrect)
                .map(ExerciseAnswer::getId)
                .collect(Collectors.toSet());
        return ExamQuestionReviewResponse.builder()
                .examQuestionId(question.getId())
                .orderIndex(question.getOrderIndex())
                .exercise(exerciseMapper.toAdminResponse(question.getExercise()))
                .selectedAnswerIds(new ArrayList<>(selectedAnswerIds))
                .correct(selectedAnswerIds.equals(correctAnswerIds))
                .build();
    }

    private AdminExamQuestionResponse toAdminQuestionResponse(ExamQuestion question) {
        return AdminExamQuestionResponse.builder()
                .id(question.getId())
                .orderIndex(question.getOrderIndex())
                .exercise(exerciseMapper.toAdminResponse(question.getExercise()))
                .build();
    }
}
