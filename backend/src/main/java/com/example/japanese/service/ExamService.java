package com.example.japanese.service;

import com.example.japanese.dto.request.ExamAnswerSubmission;
import com.example.japanese.dto.request.ExamQuestionRequest;
import com.example.japanese.dto.request.ExamRequest;
import com.example.japanese.dto.request.SubmitExamRequest;
import com.example.japanese.dto.response.AdminExamResponse;
import com.example.japanese.dto.response.ExamAttemptResponse;
import com.example.japanese.dto.response.ExamResponse;
import com.example.japanese.dto.response.ExamResultResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.Exam;
import com.example.japanese.entity.ExamAnswer;
import com.example.japanese.entity.ExamAttempt;
import com.example.japanese.entity.ExamAttemptStatus;
import com.example.japanese.entity.ExamQuestion;
import com.example.japanese.entity.Exercise;
import com.example.japanese.entity.ExerciseAnswer;
import com.example.japanese.entity.Level;
import com.example.japanese.entity.User;
import com.example.japanese.exception.ExamExpiredException;
import com.example.japanese.exception.InvalidRequestException;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.ExamMapper;
import com.example.japanese.repository.ExamAnswerRepository;
import com.example.japanese.repository.ExamAttemptRepository;
import com.example.japanese.repository.ExamRepository;
import com.example.japanese.repository.ExerciseRepository;
import com.example.japanese.repository.LevelRepository;
import com.example.japanese.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Requirements section 17-19. Grading and the exam clock are always
 * computed on the backend (section 18: "the backend must not trust the
 * frontend timer"; BR-009, BR-010) - the frontend timer is purely a UX
 * countdown, never trusted for correctness.
 */
@Service
@RequiredArgsConstructor
public class ExamService {

    private static final List<ExamAttemptStatus> CONCLUDED_STATUSES =
            List.of(ExamAttemptStatus.COMPLETED, ExamAttemptStatus.EXPIRED);

    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final ExerciseRepository exerciseRepository;
    private final LevelRepository levelRepository;
    private final UserRepository userRepository;
    private final ExamMapper examMapper;

    // ---- Admin CRUD ----

    @Transactional(readOnly = true)
    public PageResponse<AdminExamResponse> searchForAdmin(
            String keyword, Long levelId, ContentStatus status, Pageable pageable
    ) {
        Page<Exam> page = examRepository.search(blankToNull(keyword), levelId, status, pageable);
        return PageResponse.of(page, examMapper::toAdminResponse);
    }

    @Transactional(readOnly = true)
    public AdminExamResponse findByIdForAdmin(Long id) {
        return examMapper.toAdminResponse(getOrThrow(id));
    }

    @Transactional
    public AdminExamResponse create(ExamRequest request) {
        Exam exam = Exam.builder()
                .level(getLevelOrThrow(request.getLevelId()))
                .title(request.getTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .status(request.getStatus())
                .totalQuestions(request.getQuestions().size())
                .build();

        exam.getQuestions().addAll(toQuestionEntities(request.getQuestions()));

        return examMapper.toAdminResponse(examRepository.save(exam));
    }

    @Transactional
    public AdminExamResponse update(Long id, ExamRequest request) {
        Exam exam = getOrThrow(id);

        exam.setLevel(getLevelOrThrow(request.getLevelId()));
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setStatus(request.getStatus());
        exam.setTotalQuestions(request.getQuestions().size());

        // Replace the questions wholesale rather than diffing - orphanRemoval
        // on the owning side deletes whatever rows fall out of the list.
        exam.getQuestions().clear();
        exam.getQuestions().addAll(toQuestionEntities(request.getQuestions()));

        return examMapper.toAdminResponse(examRepository.save(exam));
    }

    @Transactional
    public void delete(Long id) {
        examRepository.delete(getOrThrow(id));
    }

    // ---- Public browsing (BR-006/007/008 pattern, same as Lesson) ----

    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> searchPublished(String keyword, Long levelId, Pageable pageable) {
        Page<Exam> page = examRepository.search(blankToNull(keyword), levelId, ContentStatus.PUBLISHED, pageable);
        return PageResponse.of(page, examMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> searchAllForBrowse(String keyword, Long levelId, ContentStatus status, Pageable pageable) {
        Page<Exam> page = examRepository.search(blankToNull(keyword), levelId, status, pageable);
        return PageResponse.of(page, examMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ExamResponse findPublishedById(Long id) {
        Exam exam = getOrThrow(id);
        if (exam.getStatus() != ContentStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Exam not found: " + id);
        }
        return examMapper.toResponse(exam);
    }

    @Transactional(readOnly = true)
    public ExamResponse findAnyByIdForBrowse(Long id) {
        return examMapper.toResponse(getOrThrow(id));
    }

    // ---- Attempt flow (section 18) ----

    @Transactional
    public ExamAttemptResponse start(Long userId, Long examId) {
        Exam exam = getOrThrow(examId);
        if (exam.getStatus() != ContentStatus.PUBLISHED) {
            throw new InvalidRequestException("This exam is not available to take"); // BR-008
        }

        var existing = examAttemptRepository.findByUserIdAndExamIdAndStatus(userId, examId, ExamAttemptStatus.IN_PROGRESS);
        if (existing.isPresent()) {
            ExamAttempt attempt = existing.get();
            if (isExpired(attempt, exam)) {
                attempt.setStatus(ExamAttemptStatus.EXPIRED);
                examAttemptRepository.save(attempt);
            } else {
                // Idempotent start - resume the live attempt instead of creating a duplicate.
                return toAttemptResponse(attempt, exam);
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        ExamAttempt attempt = ExamAttempt.builder()
                .user(user)
                .exam(exam)
                .startedAt(LocalDateTime.now())
                .status(ExamAttemptStatus.IN_PROGRESS)
                .score(0)
                .correctCount(0)
                .wrongCount(0)
                .build();

        return toAttemptResponse(examAttemptRepository.save(attempt), exam);
    }

    @Transactional
    public ExamResultResponse submit(Long userId, Long examId, SubmitExamRequest request) {
        Exam exam = getOrThrow(examId);
        ExamAttempt attempt = examAttemptRepository.findByUserIdAndExamIdAndStatus(userId, examId, ExamAttemptStatus.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("No active attempt for this exam"));

        if (isExpired(attempt, exam)) {
            attempt.setStatus(ExamAttemptStatus.EXPIRED);
            examAttemptRepository.save(attempt);
            throw new ExamExpiredException("This exam attempt has expired and cannot be submitted"); // BR-009
        }

        Map<Long, ExamQuestion> questionsById = exam.getQuestions().stream()
                .collect(Collectors.toMap(ExamQuestion::getId, q -> q));

        Map<Long, Set<Long>> submittedByQuestion = new HashMap<>();
        for (ExamAnswerSubmission submission : request.getAnswers()) {
            ExamQuestion question = questionsById.get(submission.getExamQuestionId());
            if (question == null) {
                throw new InvalidRequestException("examQuestionId does not belong to this exam");
            }
            Set<Long> validAnswerIds = question.getExercise().getAnswers().stream()
                    .map(ExerciseAnswer::getId)
                    .collect(Collectors.toSet());
            Set<Long> submittedIds = new HashSet<>(submission.getAnswerIds());
            if (!validAnswerIds.containsAll(submittedIds)) {
                throw new InvalidRequestException("One or more answerIds do not belong to this question");
            }
            submittedByQuestion.put(question.getId(), submittedIds);
        }

        examAnswerRepository.deleteByExamAttemptId(attempt.getId());

        int correctCount = 0;
        for (ExamQuestion question : exam.getQuestions()) {
            Set<Long> submittedIds = submittedByQuestion.getOrDefault(question.getId(), Set.of());
            Set<Long> correctAnswerIds = question.getExercise().getAnswers().stream()
                    .filter(ExerciseAnswer::isCorrect)
                    .map(ExerciseAnswer::getId)
                    .collect(Collectors.toSet());

            if (submittedIds.equals(correctAnswerIds)) {
                correctCount++;
            }

            for (ExerciseAnswer answer : question.getExercise().getAnswers()) {
                if (submittedIds.contains(answer.getId())) {
                    examAnswerRepository.save(ExamAnswer.builder()
                            .examAttempt(attempt)
                            .examQuestion(question)
                            .selectedAnswer(answer)
                            .build());
                }
            }
        }

        int totalQuestions = exam.getQuestions().size();
        int wrongCount = totalQuestions - correctCount;
        int score = totalQuestions == 0 ? 0 : Math.round(correctCount * 100.0f / totalQuestions);

        attempt.setStatus(ExamAttemptStatus.COMPLETED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setScore(score);
        attempt.setCorrectCount(correctCount);
        attempt.setWrongCount(wrongCount);
        examAttemptRepository.save(attempt);

        return toResultResponse(attempt, exam);
    }

    @Transactional(readOnly = true)
    public ExamResultResponse result(Long userId, Long examId) {
        Exam exam = getOrThrow(examId);
        ExamAttempt attempt = examAttemptRepository
                .findFirstByUserIdAndExamIdAndStatusInOrderByIdDesc(userId, examId, CONCLUDED_STATUSES)
                .orElseThrow(() -> new ResourceNotFoundException("No completed attempt found for this exam"));
        return toResultResponse(attempt, exam);
    }

    // ---- helpers ----

    private boolean isExpired(ExamAttempt attempt, Exam exam) {
        LocalDateTime deadline = attempt.getStartedAt().plusMinutes(exam.getDurationMinutes());
        return LocalDateTime.now().isAfter(deadline);
    }

    private ExamAttemptResponse toAttemptResponse(ExamAttempt attempt, Exam exam) {
        return ExamAttemptResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .durationMinutes(exam.getDurationMinutes())
                .startedAt(attempt.getStartedAt())
                .status(attempt.getStatus())
                .questions(exam.getQuestions().stream().map(examMapper::toQuestionResponse).toList())
                .build();
    }

    private ExamResultResponse toResultResponse(ExamAttempt attempt, Exam exam) {
        return ExamResultResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .status(attempt.getStatus())
                .score(attempt.getScore())
                .correctCount(attempt.getCorrectCount())
                .wrongCount(attempt.getWrongCount())
                .totalQuestions(exam.getTotalQuestions())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }

    private List<ExamQuestion> toQuestionEntities(List<ExamQuestionRequest> requests) {
        return requests.stream()
                .map(r -> ExamQuestion.builder()
                        .exercise(getExerciseOrThrow(r.getExerciseId()))
                        .orderIndex(r.getOrderIndex())
                        .build())
                .toList();
    }

    private Exam getOrThrow(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found: " + id));
    }

    private Level getLevelOrThrow(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found: " + levelId));
    }

    private Exercise getExerciseOrThrow(Long exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found: " + exerciseId));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
