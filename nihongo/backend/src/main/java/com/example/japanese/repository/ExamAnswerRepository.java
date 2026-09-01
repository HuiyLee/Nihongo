package com.example.japanese.repository;

import com.example.japanese.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, Long> {

    /** Defensive clear before re-inserting on submit/save - see ExamService.persistAnswers(). */
    void deleteByExamAttemptId(Long examAttemptId);

    /** Whatever's been auto-saved (or submitted) so far for one attempt - see ExamService.toAttemptResponse(). */
    List<ExamAnswer> findByExamAttemptId(Long examAttemptId);
}
