package com.example.japanese.service;

import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.entity.Grammar;
import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.MarkOutcome;
import com.example.japanese.entity.User;
import com.example.japanese.entity.UserGrammar;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.GrammarRepository;
import com.example.japanese.repository.UserGrammarRepository;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.util.LearningStateUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGrammarService {

    private final UserGrammarRepository userGrammarRepository;
    private final GrammarRepository grammarRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public LearningProgressResponse getProgress(Long userId, Long grammarId) {
        return userGrammarRepository.findByUserIdAndGrammarId(userId, grammarId)
                .map(UserGrammarService::toResponse)
                .orElseGet(UserGrammarService::defaultProgress);
    }

    @Transactional
    public LearningProgressResponse mark(Long userId, Long grammarId, MarkOutcome outcome) {
        Grammar grammar = grammarRepository.findById(grammarId)
                .orElseThrow(() -> new ResourceNotFoundException("Grammar not found: " + grammarId));

        UserGrammar state = userGrammarRepository.findByUserIdAndGrammarId(userId, grammarId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    UserGrammar created = new UserGrammar();
                    created.setUser(user);
                    created.setGrammar(grammar);
                    created.setStatus(LearningStatus.NEW);
                    return created;
                });

        LearningStateUpdater.apply(state, outcome);

        return toResponse(userGrammarRepository.save(state));
    }

    private static LearningProgressResponse toResponse(UserGrammar state) {
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
