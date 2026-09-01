package com.example.japanese.repository;

import com.example.japanese.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {

    List<Level> findAllByOrderByOrderIndexAsc();

    Optional<Level> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    boolean existsByOrderIndex(Integer orderIndex);

    boolean existsByOrderIndexAndIdNot(Integer orderIndex, Long id);
}
