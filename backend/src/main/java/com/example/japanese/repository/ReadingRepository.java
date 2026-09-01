package com.example.japanese.repository;

import com.example.japanese.entity.Reading;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadingRepository extends JpaRepository<Reading, Long> {

    @Query("""
            select r from Reading r
            where (:keyword is null
                or lower(r.title) like lower(concat('%', cast(:keyword as string), '%')))
              and (:levelId is null or r.level.id = :levelId)
            """)
    Page<Reading> search(
            @Param("keyword") String keyword,
            @Param("levelId") Long levelId,
            Pageable pageable
    );
}
