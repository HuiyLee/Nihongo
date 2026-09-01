package com.example.japanese.repository;

import com.example.japanese.entity.Bookmark;
import com.example.japanese.entity.BookmarkTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, BookmarkTargetType targetType, Long targetId);

    Optional<Bookmark> findByIdAndUserId(Long id, Long userId);

    Page<Bookmark> findByUserId(Long userId, Pageable pageable);

    Page<Bookmark> findByUserIdAndTargetType(Long userId, BookmarkTargetType targetType, Pageable pageable);
}
