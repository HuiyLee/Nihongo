package com.example.japanese.repository;

import com.example.japanese.entity.Vocabulary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {

    @Query("""
            select v from Vocabulary v
            where (:keyword is null
                or lower(v.word) like lower(concat('%', cast(:keyword as string), '%'))
                or lower(v.kanji) like lower(concat('%', cast(:keyword as string), '%'))
                or lower(v.hiragana) like lower(concat('%', cast(:keyword as string), '%'))
                or lower(v.katakana) like lower(concat('%', cast(:keyword as string), '%'))
                or lower(v.romaji) like lower(concat('%', cast(:keyword as string), '%'))
                or lower(v.meaning) like lower(concat('%', cast(:keyword as string), '%')))
              and (:levelId is null or v.level.id = :levelId)
              and (:lessonId is null or v.lesson.id = :lessonId)
            """)
    Page<Vocabulary> search(
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            @Param("lessonId") Long lessonId,
            Pageable pageable
    );

    /** Requirements section 20 - denominator for the Lessons progress approximation. */
    long countByLessonIsNotNull();
}
