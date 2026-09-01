package com.example.japanese.repository;

import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.UserKanji;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserKanjiRepository extends JpaRepository<UserKanji, Long> {

    Optional<UserKanji> findByUserIdAndKanjiId(Long userId, Long kanjiId);

    /** Requirements section 20 - feeds the Kanji progress percentage. */
    long countByUserIdAndStatus(Long userId, LearningStatus status);

    /** Requirements section 20 - the lesson-linked slice, feeding the Lessons progress approximation. */
    long countByUserIdAndStatusAndKanji_LessonIsNotNull(Long userId, LearningStatus status);
}
