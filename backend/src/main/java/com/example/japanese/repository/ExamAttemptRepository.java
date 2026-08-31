package com.example.japanese.repository;

import com.example.japanese.entity.ExamAttempt;
import com.example.japanese.entity.ExamAttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    Optional<ExamAttempt> findByUserIdAndExamIdAndStatus(Long userId, Long examId, ExamAttemptStatus status);

    Optional<ExamAttempt> findFirstByUserIdAndExamIdAndStatusInOrderByIdDesc(
            Long userId, Long examId, List<ExamAttemptStatus> statuses
    );
}
