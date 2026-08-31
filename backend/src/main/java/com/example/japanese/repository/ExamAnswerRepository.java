package com.example.japanese.repository;

import com.example.japanese.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {

    /** Defensive clear before re-inserting on submit - see ExamService.submit(). */
    void deleteByExamAttemptId(Long examAttemptId);
}
