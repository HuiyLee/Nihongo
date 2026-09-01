package com.example.japanese.repository;

import com.example.japanese.entity.ExamAttempt;
import com.example.japanese.entity.ExamAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    Optional<ExamAttempt> findByUserIdAndExamIdAndStatus(Long userId, Long examId, ExamAttemptStatus status);

    Optional<ExamAttempt> findFirstByUserIdAndExamIdAndStatusInOrderByIdDesc(
            Long userId, Long examId, List<ExamAttemptStatus> statuses
    );

    /** Requirements section 38 Phase 5 ("History") - every concluded attempt across every exam, newest first. */
    Page<ExamAttempt> findByUserIdAndStatusInOrderByIdDesc(
            Long userId, List<ExamAttemptStatus> statuses, Pageable pageable
    );

    /** Requirements section 20 - numerator for the Exams progress percentage (an exam counts once, however many times it was retaken). */
    @Query("select count(distinct a.exam.id) from ExamAttempt a where a.user.id = :userId and a.status = :status")
    long countDistinctExamIdByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ExamAttemptStatus status);

    /** Requirements section 35 - admin dashboard totals/pass-rate. */
    long countByStatus(ExamAttemptStatus status);

    long countByStatusAndScoreGreaterThanEqual(ExamAttemptStatus status, int score);
}
