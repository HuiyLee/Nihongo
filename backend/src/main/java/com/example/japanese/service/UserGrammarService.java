package com.example.japanese.service;

import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.entity.Grammar;
import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.MarkOutcome;
import com.example.japanese.entity.StudyActivityType;
import com.example.japanese.entity.User;
import com.example.japanese.entity.UserGrammar;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.GrammarRepository;
import com.example.japanese.repository.UserGrammarRepository;
import com.example.japanese.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserGrammarService {

    private final UserGrammarRepository userGrammarRepository;
    private final GrammarRepository grammarRepository;
    private final UserRepository userRepository;
    private final SpacedRepetitionService spacedRepetitionService;
    private final StudySessionService studySessionService;

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

        spacedRepetitionService.apply(state, outcome);
        LearningProgressResponse response = toResponse(userGrammarRepository.save(state));

        LocalDateTime now = LocalDateTime.now();
        StudySessionRequest session = new StudySessionRequest();
        session.setActivityType(StudyActivityType.GRAMMAR);
        session.setReferenceId(grammarId);
        session.setStartedAt(now);
        session.setEndedAt(now);
        studySessionService.record(userId, session);

        return response;
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
