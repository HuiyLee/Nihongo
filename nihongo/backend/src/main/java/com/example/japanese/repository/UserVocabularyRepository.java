package com.example.japanese.repository;

import com.example.japanese.entity.LearningStatus;
import com.example.japanese.entity.UserVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {

    Optional<UserVocabulary> findByUserIdAndVocabularyId(Long userId, Long vocabularyId);

    /** Requirements section 20 - feeds the Vocabulary progress percentage. */
    long countByUserIdAndStatus(Long userId, LearningStatus status);

    /** Requirements section 20 - the lesson-linked slice, feeding the Lessons progress approximation. */
    long countByUserIdAndStatusAndVocabulary_LessonIsNotNull(Long userId, LearningStatus status);
}
