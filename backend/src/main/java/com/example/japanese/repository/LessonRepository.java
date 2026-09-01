package com.example.japanese.repository;

import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    @Query("""
            select l from Lesson l
            where (:keyword is null or lower(l.title) like lower(concat('%', cast(:keyword as string), '%')))
              and (:levelId is null or l.level.id = :levelId)
              and (:status is null or l.status = :status)
            """)
    Page<Lesson> search(
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            @Param("status") ContentStatus status,
            Pageable pageable
    );
}
