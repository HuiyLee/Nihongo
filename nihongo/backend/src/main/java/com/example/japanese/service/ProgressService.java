package com.example.japanese.service;

import com.example.japanese.dto.response.CategoryProgressResponse;
import com.example.japanese.dto.response.ProgressOverviewResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.ExamAttemptStatus;
import com.example.japanese.entity.LearningStatus;
import com.example.japanese.repository.ExamAttemptRepository;
import com.example.japanese.repository.ExamRepository;
import com.example.japanese.repository.GrammarRepository;
import com.example.japanese.repository.KanjiRepository;
import com.example.japanese.repository.UserGrammarRepository;
import com.example.japanese.repository.UserKanjiRepository;
import com.example.japanese.repository.UserVocabularyRepository;
import com.example.japanese.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Requirements section 20 (Learning Progress Dashboard). Every percentage is
 * computed on the fly from existing counts - there's no cached "progress"
 * row anywhere, so this always reflects the current state of the data.
 */
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final VocabularyRepository vocabularyRepository;
    private final KanjiRepository kanjiRepository;
    private final GrammarRepository grammarRepository;
    private final UserVocabularyRepository userVocabularyRepository;
    private final UserKanjiRepository userKanjiRepository;
    private final UserGrammarRepository userGrammarRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;

    @Transactional(readOnly = true)
    public ProgressOverviewResponse overview(Long userId) {
        return ProgressOverviewResponse.builder()
                .vocabulary(vocabulary(userId))
                .kanji(kanji(userId))
                .grammar(grammar(userId))
                .lessons(lessons(userId))
                .exams(exams(userId))
                .build();
    }

    @Transactional(readOnly = true)
    public CategoryProgressResponse vocabulary(Long userId) {
        long known = userVocabularyRepository.countByUserIdAndStatus(userId, LearningStatus.KNOWN);
        return toCategory(known, vocabularyRepository.count());
    }

    @Transactional(readOnly = true)
    public CategoryProgressResponse kanji(Long userId) {
        long known = userKanjiRepository.countByUserIdAndStatus(userId, LearningStatus.KNOWN);
        return toCategory(known, kanjiRepository.count());
    }

    @Transactional(readOnly = true)
    public CategoryProgressResponse grammar(Long userId) {
        long known = userGrammarRepository.countByUserIdAndStatus(userId, LearningStatus.KNOWN);
        return toCategory(known, grammarRepository.count());
    }

    /**
     * Approximated as the fraction of lesson-linked vocabulary/kanji/grammar
     * content the user has marked KNOWN - there's no dedicated per-lesson
     * completion tracking in this data model, and the real learner-facing
     * /lessons pages are out of scope for this phase (still a ComingSoonPage
     * placeholder in AppRoutes).
     */
    @Transactional(readOnly = true)
    public CategoryProgressResponse lessons(Long userId) {
        long known = userVocabularyRepository.countByUserIdAndStatusAndVocabulary_LessonIsNotNull(userId, LearningStatus.KNOWN)
                + userKanjiRepository.countByUserIdAndStatusAndKanji_LessonIsNotNull(userId, LearningStatus.KNOWN)
                + userGrammarRepository.countByUserIdAndStatusAndGrammar_LessonIsNotNull(userId, LearningStatus.KNOWN);
        long total = vocabularyRepository.countByLessonIsNotNull()
                + kanjiRepository.countByLessonIsNotNull()
                + grammarRepository.countByLessonIsNotNull();
        return toCategory(known, total);
    }

    /** An exam counts once toward "known" however many times the user retook it. */
    @Transactional(readOnly = true)
    public CategoryProgressResponse exams(Long userId) {
        long known = examAttemptRepository.countDistinctExamIdByUserIdAndStatus(userId, ExamAttemptStatus.COMPLETED);
        return toCategory(known, examRepository.countByStatus(ContentStatus.PUBLISHED));
    }

    private CategoryProgressResponse toCategory(long known, long total) {
        int percent = total == 0 ? 0 : (int) Math.round(known * 100.0 / total);
        return CategoryProgressResponse.builder()
                .known(known)
                .total(total)
                .percent(percent)
                .build();
    }
}
