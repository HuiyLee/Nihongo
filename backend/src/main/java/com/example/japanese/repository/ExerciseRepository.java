package com.example.japanese.repository;

import com.example.japanese.entity.Exercise;
import com.example.japanese.entity.ExerciseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("""
            select e from Exercise e
            where (:keyword is null or lower(e.question) like lower(concat('%', cast(:keyword as string), '%')))
              and (:levelId is null or e.level.id = :levelId)
              and (:lessonId is null or e.lesson.id = :lessonId)
              and (:readingId is null or e.reading.id = :readingId)
              and (:type is null or e.type = :type)
            """)
    Page<Exercise> search(
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            @Param("lessonId") Long lessonId,
            @Param("readingId") Long readingId,
            @Param("type") ExerciseType type,
            Pageable pageable
    );
}
