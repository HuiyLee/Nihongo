package com.example.japanese.service;

import com.example.japanese.dto.response.StreakResponse;
import com.example.japanese.repository.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

/**
 * Requirements section 22. A day counts toward the streak if the user
 * performed at least one learning activity that day - derived from
 * StudySession rows (vocabulary/kanji/grammar review, exercise, exam,
 * reading all write one, per the Phase 6 wiring in the *Service mark()/
 * submit()/complete() methods). Computed on the fly on every call rather
 * than cached, and bucketed to LocalDate in plain Java rather than a
 * DB-dialect date-truncation function, so it behaves identically against
 * H2 (tests) and Postgres.
 */
@Service
@RequiredArgsConstructor
public class StreakService {

    private final StudySessionRepository studySessionRepository;

    @Transactional(readOnly = true)
    public StreakResponse getStreak(Long userId) {
        List<LocalDateTime> timestamps = studySessionRepository.findStartedAtByUserId(userId);

        TreeSet<LocalDate> activeDays = new TreeSet<>();
        for (LocalDateTime timestamp : timestamps) {
            activeDays.add(timestamp.toLocalDate());
        }

        return StreakResponse.builder()
                .currentStreak(currentStreak(activeDays))
                .longestStreak(longestStreak(activeDays))
                .lastActiveDate(activeDays.isEmpty() ? null : activeDays.last())
                .build();
    }

    /** Counts back from today (or yesterday, if today has no activity yet) while each day is consecutive. */
    private int currentStreak(TreeSet<LocalDate> activeDays) {
        if (activeDays.isEmpty()) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        LocalDate cursor = activeDays.contains(today) ? today : today.minusDays(1);
        if (!activeDays.contains(cursor)) {
            return 0;
        }
        int streak = 0;
        while (activeDays.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private int longestStreak(TreeSet<LocalDate> activeDays) {
        int longest = 0;
        int current = 0;
        LocalDate previous = null;
        for (LocalDate day : activeDays) {
            if (previous != null && previous.plusDays(1).equals(day)) {
                current++;
            } else {
                current = 1;
            }
            longest = Math.max(longest, current);
            previous = day;
        }
        return longest;
    }
}
