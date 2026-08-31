package com.example.japanese.service;

import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.MarkOutcome;
import com.example.japanese.entity.User;
import com.example.japanese.entity.UserVocabulary;
import com.example.japanese.entity.Vocabulary;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.repository.UserVocabularyRepository;
import com.example.japanese.repository.VocabularyRepository;
import com.example.japanese.util.LearningStateUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserVocabularyService {

    private final UserVocabularyRepository userVocabularyRepository;
    private final VocabularyRepository vocabularyRepository;
    private final UserRepository userRepository;

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

        LearningStateUpdater.apply(state, outcome);

        return toResponse(userVocabularyRepository.save(state));
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
