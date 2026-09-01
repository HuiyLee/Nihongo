package com.example.japanese.service;

import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.MarkOutcome;
import com.example.japanese.entity.UserLearningItem;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Requirements section 11 (Spaced Repetition). A dedicated, injectable
 * service - deliberately not static and not inlined into a controller -
 * so the algorithm can be swapped out later without touching
 * UserVocabularyService/UserKanjiService/UserGrammarService. Replaces the
 * Phase 3 placeholder util/LearningStateUpdater.
 *
 * <p>UserLearningItem has no dedicated intervalDays column, so the
 * previous interval is derived from the gap between the item's previous
 * lastReviewedAt and nextReviewAt, read before either field is
 * overwritten by this call. An item that has never been scheduled before
 * (either field null) has no such gap and starts at INITIAL_INTERVAL_DAYS.
 *
 * <p>Correct: interval doubles (capped at MAX_INTERVAL_DAYS), pushing the
 * next review further out. Incorrect: interval halves (floored at
 * MIN_INTERVAL_DAYS) and status drops to LEARNING, bringing the next
 * review sooner.
 */
@Service
public class SpacedRepetitionService {

    private static final long INITIAL_INTERVAL_DAYS = 1;
    private static final long MIN_INTERVAL_DAYS = 1;
    private static final long MAX_INTERVAL_DAYS = 90;

    public void apply(UserLearningItem item, MarkOutcome outcome) {
        long previousIntervalDays = previousIntervalDays(item);
        LocalDateTime now = LocalDateTime.now();

        item.setLastReviewedAt(now);

        long nextIntervalDays;
        if (outcome == MarkOutcome.KNOWN) {
            item.setCorrectCount(item.getCorrectCount() + 1);
            item.setStatus(LearningStatus.KNOWN);
            nextIntervalDays = previousIntervalDays <= 0
                    ? INITIAL_INTERVAL_DAYS
                    : Math.min(previousIntervalDays * 2, MAX_INTERVAL_DAYS);
        } else {
            item.setWrongCount(item.getWrongCount() + 1);
            item.setStatus(LearningStatus.LEARNING);
            nextIntervalDays = Math.max(MIN_INTERVAL_DAYS, previousIntervalDays / 2);
        }

        item.setNextReviewAt(now.plusDays(nextIntervalDays));
    }

    private long previousIntervalDays(UserLearningItem item) {
        if (item.getLastReviewedAt() == null || item.getNextReviewAt() == null) {
            return 0;
        }
        long days = Duration.between(item.getLastReviewedAt(), item.getNextReviewAt()).toDays();
        return Math.max(days, 0);
    }
}
