package com.example.japanese.repository;

import com.example.japanese.entity.UserKanji;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserKanjiRepository extends JpaRepository<UserKanji, Long> {

    Optional<UserKanji> findByUserIdAndKanjiId(Long userId, Long kanjiId);
}
