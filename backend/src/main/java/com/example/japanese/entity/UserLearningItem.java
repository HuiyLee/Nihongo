package com.example.japanese.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Shared fields for a user's per-item learning state (UserVocabulary,
 * UserKanji, UserGrammar - requirements section 10 and section 30). Each
 * subclass adds its own target FK (vocabulary/kanji/grammar) since JPA
 * cannot express a single polymorphic association across three unrelated
 * entities without unnecessary complexity for this phase.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class UserLearningItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LearningStatus status = LearningStatus.NEW;

    @Column(name = "correct_count", nullable = false)
    private int correctCount = 0;

    @Column(name = "wrong_count", nullable = false)
    private int wrongCount = 0;

    @Column(name = "last_reviewed_at")
    private LocalDateTime lastReviewedAt;

    /** Left null until Phase 6 introduces the dedicated SpacedRepetitionService (section 11). */
    @Column(name = "next_review_at")
    private LocalDateTime nextReviewAt;
}
