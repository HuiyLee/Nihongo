package com.example.japanese.repository;

import com.example.japanese.entity.ExamAttempt;
import com.example.japanese.entity.ExamAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
