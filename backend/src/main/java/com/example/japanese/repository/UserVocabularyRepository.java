package com.example.japanese.repository;

import com.example.japanese.entity.UserVocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {

    Optional<UserVocabulary> findByUserIdAndVocabularyId(Long userId, Long vocabularyId);
}
