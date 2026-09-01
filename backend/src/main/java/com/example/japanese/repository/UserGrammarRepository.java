package com.example.japanese.repository;

import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.UserGrammar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGrammarRepository extends JpaRepository<UserGrammar, Long> {

    Optional<UserGrammar> findByUserIdAndGrammarId(Long userId, Long grammarId);

    /** Requirements section 20 - feeds the Grammar progress percentage. */
    long countByUserIdAndStatus(Long userId, LearningStatus status);

    /** Requirements section 20 - the lesson-linked slice, feeding the Lessons progress approximation. */
    long countByUserIdAndStatusAndGrammar_LessonIsNotNull(Long userId, LearningStatus status);
}
