package com.example.japanese.service;

import com.example.japanese.dto.request.LevelRequest;
import com.example.japanese.dto.response.LevelResponse;
import com.example.japanese.entity.Level;
import com.example.japanese.exception.DuplicateResourceException;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.LevelMapper;
import com.example.japanese.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;
    private final LevelMapper levelMapper;

    @Transactional(readOnly = true)
    public List<LevelResponse> findAll() {
        return levelRepository.findAllByOrderByOrderIndexAsc().stream()
                .map(levelMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LevelResponse findById(Long id) {
        return levelMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public LevelResponse create(LevelRequest request) {
        validateUnique(request, null);

        Level level = Level.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex())
                .status(request.getStatus())
                .build();

        return levelMapper.toResponse(levelRepository.save(level));
    }

    @Transactional
    public LevelResponse update(Long id, LevelRequest request) {
        Level level = getOrThrow(id);
        validateUnique(request, id);

        level.setCode(request.getCode());
        level.setName(request.getName());
        level.setDescription(request.getDescription());
        level.setOrderIndex(request.getOrderIndex());
        level.setStatus(request.getStatus());

        return levelMapper.toResponse(levelRepository.save(level));
    }

    @Transactional
    public void delete(Long id) {
        Level level = getOrThrow(id);
        levelRepository.delete(level);
    }

    private Level getOrThrow(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found: " + id));
    }

    private void validateUnique(LevelRequest request, Long excludeId) {
        boolean codeTaken = excludeId == null
                ? levelRepository.existsByCode(request.getCode())
                : levelRepository.existsByCodeAndIdNot(request.getCode(), excludeId);
        if (codeTaken) {
            throw new DuplicateResourceException("Level code already exists: " + request.getCode());
        }

        boolean orderIndexTaken = excludeId == null
                ? levelRepository.existsByOrderIndex(request.getOrderIndex())
                : levelRepository.existsByOrderIndexAndIdNot(request.getOrderIndex(), excludeId);
        if (orderIndexTaken) {
            throw new DuplicateResourceException("Level orderIndex already exists: " + request.getOrderIndex());
        }
    }
}
