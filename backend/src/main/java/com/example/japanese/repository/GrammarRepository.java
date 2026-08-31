package com.example.japanese.repository;

import com.example.japanese.entity.Grammar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GrammarRepository extends JpaRepository<Grammar, Long> {

    @Query("""
            select g from Grammar g
            where (:keyword is null
                or lower(g.pattern) like lower(concat('%', :keyword, '%'))
                or lower(g.meaning) like lower(concat('%', :keyword, '%')))
              and (:levelId is null or g.level.id = :levelId)
              and (:lessonId is null or g.lesson.id = :lessonId)
            """)
    Page<Grammar> search(
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            @Param("lessonId") Long lessonId,
            Pageable pageable
    );
}
