package com.example.japanese.service;

import com.example.japanese.dto.response.AdminStatsResponse;
import com.example.japanese.entity.ExamAttemptStatus;
import com.example.japanese.repository.ExamAttemptRepository;
import com.example.japanese.repository.ExamRepository;
import com.example.japanese.repository.ExerciseRepository;
import com.example.japanese.repository.GrammarRepository;
import com.example.japanese.repository.KanjiRepository;
import com.example.japanese.repository.LessonRepository;
import com.example.japanese.repository.StudySessionRepository;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Requirements section 35 (Admin Dashboard). */
@Service
@RequiredArgsConstructor
public class AdminStatsService {

    /** Matches the pass threshold already hardcoded in ExamResultPage.tsx (passed = score >= 60). */
    private static final int PASS_THRESHOLD = 60;

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final VocabularyRepository vocabularyRepository;
    private final KanjiRepository kanjiRepository;
    private final GrammarRepository grammarRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExamRepository examRepository;
    private final StudySessionRepository studySessionRepository;
    private final ExamAttemptRepository examAttemptRepository;

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        long completedAttempts = examAttemptRepository.countByStatus(ExamAttemptStatus.COMPLETED);
        long passedAttempts = examAttemptRepository.countByStatusAndScoreGreaterThanEqual(
                ExamAttemptStatus.COMPLETED, PASS_THRESHOLD
        );
        double passRate = completedAttempts == 0
                ? 0.0
                : Math.round(passedAttempts * 1000.0 / completedAttempts) / 10.0;

        return AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalLessons(lessonRepository.count())
                .totalVocabulary(vocabularyRepository.count())
                .totalKanji(kanjiRepository.count())
                .totalGrammar(grammarRepository.count())
                .totalExercises(exerciseRepository.count())
                .totalExams(examRepository.count())
                .totalStudySessions(studySessionRepository.count())
                .totalExamAttempts(examAttemptRepository.count())
                .passRate(passRate)
                .build();
    }
}
