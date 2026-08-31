package com.example.japanese.repository;

import com.example.japanese.entity.Kanji;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KanjiRepository extends JpaRepository<Kanji, Long> {

    @Query("""
            select k from Kanji k
            where (:keyword is null
                or lower(k.character) like lower(concat('%', :keyword, '%'))
                or lower(k.meaning) like lower(concat('%', :keyword, '%'))
                or lower(k.onyomi) like lower(concat('%', :keyword, '%'))
                or lower(k.kunyomi) like lower(concat('%', :keyword, '%')))
              and (:levelId is null or k.level.id = :levelId)
              and (:lessonId is null or k.lesson.id = :lessonId)
            """)
    Page<Kanji> search(
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            @Param("lessonId") Long lessonId,
            Pageable pageable
    );
}
