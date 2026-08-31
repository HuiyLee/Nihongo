package com.example.japanese.repository;

import com.example.japanese.entity.StudySession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    Page<StudySession> findByUserId(Long userId, Pageable pageable);
}
