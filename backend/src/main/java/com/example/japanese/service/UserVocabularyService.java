package com.example.japanese.service;

import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.MarkOutcome;
import com.example.japanese.entity.StudyActivityType;
import com.example.japanese.entity.User;
import com.example.japanese.entity.UserVocabulary;
import com.example.japanese.entity.Vocabulary;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.repository.UserVocabularyRepository;
import com.example.japanese.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserVocabularyService {

    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;
    private final SpacedRepetitionService spacedRepetitionService;
    private final StudySessionService studySessionService;

    @Transactional(readOnly = true)
    public LearningProgressResponse getProgress(Long userId, Long vocabularyId) {
        return userVocabularyRepository.findByUserIdAndVocabularyId(userId, vocabularyId)
                .map(UserVocabularyService::toResponse)
                .orElseGet(UserVocabularyService::defaultProgress);
    }

    @Transactional
    public LearningProgressResponse mark(Long userId, Long vocabularyId, MarkOutcome outcome) {
        Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary not found: " + vocabularyId));

        UserVocabulary state = userVocabularyRepository.findByUserIdAndVocabularyId(userId, vocabularyId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    UserVocabulary created = new UserVocabulary();
                    created.setUser(user);
                    created.setVocabulary(vocabulary);
                    created.setStatus(LearningStatus.NEW);
                    return created;
                });

        spacedRepetitionService.apply(state, outcome);
        LearningProgressResponse response = toResponse(userVocabularyRepository.save(state));

        // Requirements section 21/22 - every review feeds the streak calculation.
        LocalDateTime now = LocalDateTime.now();
        StudySessionRequest session = new StudySessionRequest();
        session.setActivityType(StudyActivityType.VOCABULARY);
        session.setReferenceId(vocabularyId);
        session.setStartedAt(now);
        session.setEndedAt(now);
        studySessionService.record(userId, session);

        return response;
    }

    private static LearningProgressResponse toResponse(UserVocabulary state) {
        return LearningProgressResponse.builder()
                .status(state.getStatus())
                .correctCount(state.getCorrectCount())
                .wrongCount(state.getWrongCount())
                .lastReviewedAt(state.getLastReviewedAt())
                .nextReviewAt(state.getNextReviewAt())
                .build();
    }

    private static LearningProgressResponse defaultProgress() {
        return LearningProgressResponse.builder()
                .status(LearningStatus.NEW)
                .correctCount(0)
                .wrongCount(0)
                .build();
    }
}
