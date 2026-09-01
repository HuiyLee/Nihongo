package com.example.japanese.service;

import com.example.japanese.dto.request.ExerciseAnswerRequest;
import com.example.japanese.dto.request.ExerciseRequest;
import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.request.SubmitExerciseRequest;
import com.example.japanese.dto.response.AdminExerciseResponse;
import com.example.japanese.dto.response.ExerciseResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.SubmitExerciseResponse;
import com.example.japanese.entity.Exercise;
import com.example.japanese.entity.ExerciseAnswer;
import com.example.japanese.entity.ExerciseType;
import com.example.japanese.entity.Lesson;
import com.example.japanese.entity.Level;
import com.example.japanese.entity.Reading;
import com.example.japanese.entity.StudyActivityType;
import com.example.japanese.exception.InvalidRequestException;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.ExerciseMapper;
import com.example.japanese.repository.ExerciseRepository;
import com.example.japanese.repository.LessonRepository;
import com.example.japanese.repository.LevelRepository;
import com.example.japanese.repository.ReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requirements section 14. Exercise submissions are graded statelessly -
 * there is no exercise_attempts table in this data model (unlike exams,
 * section 17-19), so submit() never writes anything, it just checks the
 * submitted answer set against the stored correct-answer set.
 */
@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;
    private final ReadingRepository readingRepository;
    private final ExerciseMapper exerciseMapper;
    private final StudySessionService studySessionService;

    @Transactional(readOnly = true)
    public PageResponse<AdminExerciseResponse> searchForAdmin(
            String keyword, Long levelId, Long lessonId, Long readingId, ExerciseType type, Pageable pageable
    ) {
        Page<Exercise> page = exerciseRepository.search(blankToNull(keyword), levelId, lessonId, readingId, type, pageable);
        return PageResponse.of(page, exerciseMapper::toAdminResponse);
    }

    @Transactional(readOnly = true)
    public AdminExerciseResponse findByIdForAdmin(Long id) {
        return exerciseMapper.toAdminResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ExerciseResponse> search(
            String keyword, Long levelId, Long lessonId, Long readingId, ExerciseType type, Pageable pageable
    ) {
        Page<Exercise> page = exerciseRepository.search(blankToNull(keyword), levelId, lessonId, readingId, type, pageable);
        return PageResponse.of(page, exerciseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ExerciseResponse findById(Long id) {
        return exerciseMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public AdminExerciseResponse create(ExerciseRequest request) {
        Exercise exercise = Exercise.builder()
                .lesson(resolveLesson(request.getLessonId()))
                .reading(resolveReading(request.getReadingId()))
                .level(getLevelOrThrow(request.getLevelId()))
                .type(request.getType())
                .question(request.getQuestion())
                .explanation(request.getExplanation())
                .audioUrl(request.getAudioUrl())
                .imageUrl(request.getImageUrl())
                .difficulty(request.getDifficulty())
                .build();

        exercise.getAnswers().addAll(toAnswerEntities(request.getAnswers()));

        return exerciseMapper.toAdminResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public AdminExerciseResponse update(Long id, ExerciseRequest request) {
        Exercise exercise = getOrThrow(id);

        exercise.setLesson(resolveLesson(request.getLessonId()));
        exercise.setReading(resolveReading(request.getReadingId()));
        exercise.setLevel(getLevelOrThrow(request.getLevelId()));
        exercise.setType(request.getType());
        exercise.setQuestion(request.getQuestion());
        exercise.setExplanation(request.getExplanation());
        exercise.setAudioUrl(request.getAudioUrl());
        exercise.setImageUrl(request.getImageUrl());
        exercise.setDifficulty(request.getDifficulty());

        // Replace the answers wholesale rather than diffing - orphanRemoval
        // on the owning side deletes whatever rows fall out of the list.
        exercise.getAnswers().clear();
        exercise.getAnswers().addAll(toAnswerEntities(request.getAnswers()));

        return exerciseMapper.toAdminResponse(exerciseRepository.save(exercise));
    }

    @Transactional
    public void delete(Long id) {
        exerciseRepository.delete(getOrThrow(id));
    }

    @Transactional
    public SubmitExerciseResponse submit(Long userId, Long exerciseId, SubmitExerciseRequest request) {
        Exercise exercise = getOrThrow(exerciseId);

        Set<Long> validAnswerIds = exercise.getAnswers().stream()
                .map(ExerciseAnswer::getId)
                .collect(Collectors.toSet());
        Set<Long> submittedIds = new HashSet<>(request.getAnswerIds());

        if (!validAnswerIds.containsAll(submittedIds)) {
            throw new InvalidRequestException("One or more answerIds do not belong to this exercise");
        }

        Set<Long> correctAnswerIds = exercise.getAnswers().stream()
                .filter(ExerciseAnswer::isCorrect)
                .map(ExerciseAnswer::getId)
                .collect(Collectors.toSet());

        // Exact match: every correct option chosen, no incorrect option
        // chosen - works uniformly for single-answer and multi-answer types.
        boolean correct = submittedIds.equals(correctAnswerIds);

        // Requirements section 15/21/22 - only Listening exercises count as a
        // dedicated study activity for the streak; the other exercise types
        // are graded practice without their own StudySession row.
        if (exercise.getType() == ExerciseType.LISTENING) {
            LocalDateTime now = LocalDateTime.now();
            StudySessionRequest session = new StudySessionRequest();
            session.setActivityType(StudyActivityType.LISTENING);
            session.setReferenceId(exerciseId);
            session.setStartedAt(now);
            session.setEndedAt(now);
            studySessionService.record(userId, session);
        }

        return SubmitExerciseResponse.builder()
                .correct(correct)
                .score(correct ? 1 : 0)
                .explanation(exercise.getExplanation())
                .build();
    }

    private List<ExerciseAnswer> toAnswerEntities(List<ExerciseAnswerRequest> requests) {
        return requests.stream()
                .map(r -> ExerciseAnswer.builder()
                        .answerText(r.getAnswerText())
                        .correct(Boolean.TRUE.equals(r.getCorrect()))
                        .orderIndex(r.getOrderIndex())
                        .build())
                .toList();
    }

    private Exercise getOrThrow(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found: " + id));
    }

    private Level getLevelOrThrow(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found: " + levelId));
    }

    private Lesson resolveLesson(Long lessonId) {
        if (lessonId == null) {
            return null;
        }
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + lessonId));
    }

    private Reading resolveReading(Long readingId) {
        if (readingId == null) {
            return null;
        }
        return readingRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading not found: " + readingId));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
