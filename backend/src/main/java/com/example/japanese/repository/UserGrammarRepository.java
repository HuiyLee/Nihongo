package com.example.japanese.repository;

import com.example.japanese.entity.UserGrammar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserGrammarRepository extends JpaRepository<UserGrammar, Long> {

    Optional<UserGrammar> findByUserIdAndGrammarId(Long userId, Long grammarId);
}
