package com.example.japanese.util;

import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.MarkOutcome;
import com.example.japanese.entity.UserLearningItem;

import java.time.LocalDateTime;

/**
 * Applies a KNOWN/UNKNOWN review outcome to a UserVocabulary/UserKanji/
 * UserGrammar row (requirements section 10). Shared by the three services
 * so the same rule is not duplicated three times.
 *
 * <p>This is intentionally simple bookkeeping only - the real spaced
 * repetition interval calculation (section 11) belongs in a dedicated
 * SpacedRepetitionService added in a later phase, not here.
 */
public final class LearningStateUpdater {

    private LearningStateUpdater() {
    }

    public static void apply(UserLearningItem item, MarkOutcome outcome) {
        item.setLastReviewedAt(LocalDateTime.now());
        if (outcome == MarkOutcome.KNOWN) {
            item.setCorrectCount(item.getCorrectCount() + 1);
            item.setStatus(LearningStatus.KNOWN);
        } else {
            item.setWrongCount(item.getWrongCount() + 1);
            item.setStatus(LearningStatus.LEARNING);
        }
    }
}
