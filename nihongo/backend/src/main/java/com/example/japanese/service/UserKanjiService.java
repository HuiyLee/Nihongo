package com.example.japanese.service;

import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.response.LearningProgressResponse;
import com.example.japanese.entity.Kanji;
import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.MarkOutcome;
import com.example.japanese.entity.StudyActivityType;
import com.example.japanese.entity.User;
import com.example.japanese.entity.UserKanji;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.KanjiRepository;
import com.example.japanese.repository.UserKanjiRepository;
import com.example.japanese.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserKanjiService {

    private final UserKanjiRepository userKanjiRepository;
    private final KanjiRepository kanjiRepository;
    private final UserRepository userRepository;
    private final SpacedRepetitionService spacedRepetitionService;
    private final StudySessionService studySessionService;

    @Transactional(readOnly = true)
    public LearningProgressResponse getProgress(Long userId, Long kanjiId) {
        return userKanjiRepository.findByUserIdAndKanjiId(userId, kanjiId)
                .map(UserKanjiService::toResponse)
                .orElseGet(UserKanjiService::defaultProgress);
    }

    @Transactional
    public LearningProgressResponse mark(Long userId, Long kanjiId, MarkOutcome outcome) {
        Kanji kanji = kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji not found: " + kanjiId));

        UserKanji state = userKanjiRepository.findByUserIdAndKanjiId(userId, kanjiId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
                    UserKanji created = new UserKanji();
                    created.setUser(user);
                    created.setKanji(kanji);
                    created.setStatus(LearningStatus.NEW);
                    return created;
                });

        spacedRepetitionService.apply(state, outcome);
        LearningProgressResponse response = toResponse(userKanjiRepository.save(state));

        LocalDateTime now = LocalDateTime.now();
        StudySessionRequest session = new StudySessionRequest();
        session.setActivityType(StudyActivityType.KANJI);
        session.setReferenceId(kanjiId);
        session.setStartedAt(now);
        session.setEndedAt(now);
        studySessionService.record(userId, session);

        return response;
    }

    private static LearningProgressResponse toResponse(UserKanji state) {
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
