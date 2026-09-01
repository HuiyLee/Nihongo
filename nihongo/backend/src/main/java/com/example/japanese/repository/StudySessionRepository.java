package com.example.japanese.repository;

import com.example.japanese.entity.StudyActivityType;
import com.example.japanese.entity.StudySession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    Page<StudySession> findByUserId(Long userId, Pageable pageable);

    /** Requirements section 16 - has this user already completed this reading passage? */
    boolean existsByUserIdAndActivityTypeAndReferenceId(
            Long userId, StudyActivityType activityType, Long referenceId
    );

    /**
     * Requirements section 22 (Streak). Deliberately returns raw timestamps
     * rather than DB-truncated dates - bucketing to LocalDate happens in
     * plain Java (StreakService) so the same code works against both H2
     * (tests) and Postgres without a dialect-specific date function.
     */
    @Query("select s.startedAt from StudySession s where s.user.id = :userId")
    List<LocalDateTime> findStartedAtByUserId(@Param("userId") Long userId);
}
