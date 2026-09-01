package com.example.japanese.repository;

import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    /** Requirements section 20 - denominator for the Exams progress percentage. */
    long countByStatus(ContentStatus status);

    @Query("""
            select e from Exam e
            where (:keyword is null or lower(e.title) like lower(concat('%', cast(:keyword as string), '%')))
              and (:levelId is null or e.level.id = :levelId)
              and (:status is null or e.status = :status)
            """)
    Page<Exam> search(
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            @Param("status") ContentStatus status,
            Pageable pageable
    );
}
