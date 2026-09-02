package com.example.japanese.service;

import com.example.japanese.dto.request.ExamAnswerSubmission;
import com.example.japanese.dto.request.ExamQuestionRequest;
import com.example.japanese.dto.request.ExamRequest;
import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.request.SubmitExamRequest;
import com.example.japanese.dto.response.AdminExamResponse;
import com.example.japanese.dto.response.ExamAttemptResponse;
import com.example.japanese.dto.response.ExamQuestionReviewResponse;
import com.example.japanese.dto.response.ExamResponse;
import com.example.japanese.dto.response.ExamResultResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.SavedAnswerResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.Exam;
import com.example.japanese.entity.ExamAnswer;
import com.example.japanese.entity.ExamAttempt;
import com.example.japanese.entity.ExamAttemptStatus;
import com.example.japanese.entity.ExamQuestion;
import com.example.japanese.entity.Exercise;
import com.example.japanese.entity.ExerciseAnswer;
import com.example.japanese.entity.Level;
import com.example.japanese.entity.NotificationType;
import com.example.japanese.entity.StudyActivityType;
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
    private final StudySessionService studySessionService;
    private final NotificationService notificationService;

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

        AdminExamResponse response = examMapper.toAdminResponse(examRepository.save(exam));
        // A brand new exam has no previous status to compare against - treat
        // it as "was not published" so creating one directly as PUBLISHED still notifies.
        notifyIfNewlyPublished(null, exam);
        return response;
    }

    @Transactional
    public AdminExamResponse update(Long id, ExamRequest request) {
        Exam exam = getOrThrow(id);
        ContentStatus previousStatus = exam.getStatus();

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

        AdminExamResponse response = examMapper.toAdminResponse(examRepository.save(exam));
        notifyIfNewlyPublished(previousStatus, exam);
        return response;
    }

    /**
     * Requirements section 24 - fires only on the DRAFT (or nonexistent) ->
     * PUBLISHED transition, never on every save of already-published content.
     */
    private void notifyIfNewlyPublished(ContentStatus previousStatus, Exam exam) {
        boolean wasPublished = previousStatus == ContentStatus.PUBLISHED;
        boolean isPublished = exam.getStatus() == ContentStatus.PUBLISHED;
        if (!wasPublished && isPublished) {
            notificationService.notifyAllUsers(
                    NotificationType.NEW_EXAM,
                    "New exam published",
                    exam.getTitle() + " is now available"
            );
        }
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

        Map<Long, Set<Long>> submittedByQuestion = validateSubmissions(exam, request.getAnswers());
        persistAnswers(attempt, exam, submittedByQuestion);

        // Grading (BR-010) - always server-computed, never trusts a score from the request.
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

        // Requirements section 21/22 - feeds the streak calculation, using the attempt's real times.
        StudySessionRequest session = new StudySessionRequest();
        session.setActivityType(StudyActivityType.EXAM);
        session.setReferenceId(examId);
        session.setStartedAt(attempt.getStartedAt());
        session.setEndedAt(attempt.getSubmittedAt());
        studySessionService.record(userId, session);

        return toResultResponse(attempt, exam, true);
    }

    /**
     * Requirements section 38 Phase 5 ("Auto save"). Persists selections for
     * a still-IN_PROGRESS attempt without grading or changing its status -
     * called periodically by the frontend so a refresh/crash mid-attempt
     * doesn't lose answers. Subject to the same deadline as submit() (BR-009):
     * once expired, neither saving nor submitting is allowed.
     */
    @Transactional
    public void saveProgress(Long userId, Long examId, SubmitExamRequest request) {
        Exam exam = getOrThrow(examId);
        ExamAttempt attempt = examAttemptRepository.findByUserIdAndExamIdAndStatus(userId, examId, ExamAttemptStatus.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("No active attempt for this exam"));

        if (isExpired(attempt, exam)) {
            attempt.setStatus(ExamAttemptStatus.EXPIRED);
            examAttemptRepository.save(attempt);
            throw new ExamExpiredException("This exam attempt has expired and can no longer be saved");
        }

        persistAnswers(attempt, exam, validateSubmissions(exam, request.getAnswers()));
    }

    @Transactional(readOnly = true)
    public ExamResultResponse result(Long userId, Long examId) {
        Exam exam = getOrThrow(examId);
        ExamAttempt attempt = examAttemptRepository
                .findFirstByUserIdAndExamIdAndStatusInOrderByIdDesc(userId, examId, CONCLUDED_STATUSES)
                .orElseThrow(() -> new ResourceNotFoundException("No completed attempt found for this exam"));
        return toResultResponse(attempt, exam, true);
    }

    /** Requirements section 38 Phase 5 ("History") - every concluded attempt across every exam, newest first. */
    @Transactional(readOnly = true)
    public PageResponse<ExamResultResponse> history(Long userId, Pageable pageable) {
        Page<ExamAttempt> page = examAttemptRepository.findByUserIdAndStatusInOrderByIdDesc(userId, CONCLUDED_STATUSES, pageable);
        return PageResponse.of(page, attempt -> toResultResponse(attempt, attempt.getExam()));
    }

    // ---- helpers ----

    /** Validates that every submitted examQuestionId/answerIds pair actually belongs to this exam. */
    private Map<Long, Set<Long>> validateSubmissions(Exam exam, List<ExamAnswerSubmission> submissions) {
        Map<Long, ExamQuestion> questionsById = exam.getQuestions().stream()
                .collect(Collectors.toMap(ExamQuestion::getId, q -> q));

        Map<Long, Set<Long>> submittedByQuestion = new HashMap<>();
        for (ExamAnswerSubmission submission : submissions) {
            ExamQuestion question = questionsById.get(submission.getExamQuestionId());
            if (question == null) {
                throw new InvalidRequestException("examQuestionId does not belong to this exam");
            }
            Set<Long> validAnswerIds = question.getExercise().getAnswers().stream()
                    .map(ExerciseAnswer::getId)
                    .collect(Collectors.toSet());
            // answerIds is allowed to be null/empty (ExamAnswerSubmission - an
            // unanswered question is a normal submission, not a validation error).
            Set<Long> submittedIds = submission.getAnswerIds() == null
                    ? new HashSet<>()
                    : new HashSet<>(submission.getAnswerIds());
            if (!validAnswerIds.containsAll(submittedIds)) {
                throw new InvalidRequestException("One or more answerIds do not belong to this question");
            }
            submittedByQuestion.put(question.getId(), submittedIds);
        }
        return submittedByQuestion;
    }

    /** Replaces this attempt's stored answers wholesale - shared by submit() and saveProgress(). */
    private void persistAnswers(ExamAttempt attempt, Exam exam, Map<Long, Set<Long>> submittedByQuestion) {
        examAnswerRepository.deleteByExamAttemptId(attempt.getId());
        for (ExamQuestion question : exam.getQuestions()) {
            Set<Long> submittedIds = submittedByQuestion.getOrDefault(question.getId(), Set.of());
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
    }

    private boolean isExpired(ExamAttempt attempt, Exam exam) {
        LocalDateTime deadline = attempt.getStartedAt().plusMinutes(exam.getDurationMinutes());
        return LocalDateTime.now().isAfter(deadline);
    }

    private ExamAttemptResponse toAttemptResponse(ExamAttempt attempt, Exam exam) {
        Map<Long, List<Long>> savedByQuestion = examAnswerRepository.findByExamAttemptId(attempt.getId()).stream()
                .collect(Collectors.groupingBy(
                        a -> a.getExamQuestion().getId(),
                        Collectors.mapping(a -> a.getSelectedAnswer().getId(), Collectors.toList())
                ));
        List<SavedAnswerResponse> savedAnswers = savedByQuestion.entrySet().stream()
                .map(e -> SavedAnswerResponse.builder().examQuestionId(e.getKey()).answerIds(e.getValue()).build())
                .toList();

        return ExamAttemptResponse.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .durationMinutes(exam.getDurationMinutes())
                .startedAt(attempt.getStartedAt())
                .status(attempt.getStatus())
                .questions(exam.getQuestions().stream().map(examMapper::toQuestionResponse).toList())
                .savedAnswers(savedAnswers)
                .build();
    }

    /** History listing - aggregate score only, no per-question answer key (keeps each page light). */
    private ExamResultResponse toResultResponse(ExamAttempt attempt, Exam exam) {
        return toResultResponse(attempt, exam, false);
    }

    /**
     * includeReview=true attaches the full per-question answer key ("đáp án")
     * - what the learner picked vs. the correct answer(s) for every question -
     * used by submit() and the single-attempt result() endpoint. History
     * listing skips it (see the two-arg overload above) since it would repeat
     * every question's full exercise+answers payload once per past attempt.
     */
    private ExamResultResponse toResultResponse(ExamAttempt attempt, Exam exam, boolean includeReview) {
        List<ExamQuestionReviewResponse> questions = null;
        if (includeReview) {
            Map<Long, Set<Long>> selectedByQuestion = examAnswerRepository.findByExamAttemptId(attempt.getId()).stream()
                    .collect(Collectors.groupingBy(
                            a -> a.getExamQuestion().getId(),
                            Collectors.mapping(a -> a.getSelectedAnswer().getId(), Collectors.toSet())
                    ));
            questions = exam.getQuestions().stream()
                    .map(q -> examMapper.toReviewQuestionResponse(q, selectedByQuestion.getOrDefault(q.getId(), Set.of())))
                    .toList();
        }

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
                .questions(questions)
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
