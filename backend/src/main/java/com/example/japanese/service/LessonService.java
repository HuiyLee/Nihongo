package com.example.japanese.service;

import com.example.japanese.dto.request.LessonRequest;
import com.example.japanese.dto.response.LessonResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.entity.Level;
import com.example.japanese.entity.Lesson;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.LessonMapper;
import com.example.japanese.repository.LevelRepository;
import com.example.japanese.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LevelRepository levelRepository;
    private final LessonMapper lessonMapper;

    /** Admin can see lessons in any status (BR-007). */
    @Transactional(readOnly = true)
    public PageResponse<LessonResponse> searchForAdmin(String keyword, Long levelId, ContentStatus status, Pageable pageable) {
        Page<Lesson> page = lessonRepository.search(blankToNull(keyword), levelId, status, pageable);
        return PageResponse.of(page, lessonMapper::toResponse);
    }

    /** Regular users only ever see PUBLISHED lessons, regardless of what status they ask for (BR-006). */
    @Transactional(readOnly = true)
    public PageResponse<LessonResponse> searchPublished(String keyword, Long levelId, Pageable pageable) {
        Page<Lesson> page = lessonRepository.search(blankToNull(keyword), levelId, ContentStatus.PUBLISHED, pageable);
        return PageResponse.of(page, lessonMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public LessonResponse findByIdForAdmin(Long id) {
        return lessonMapper.toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public LessonResponse findPublishedById(Long id) {
        Lesson lesson = getOrThrow(id);
        if (lesson.getStatus() != ContentStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Lesson not found: " + id);
        }
        return lessonMapper.toResponse(lesson);
    }

    @Transactional
    public LessonResponse create(LessonRequest request) {
        Level level = getLevelOrThrow(request.getLevelId());

        Lesson lesson = Lesson.builder()
                .level(level)
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .orderIndex(request.getOrderIndex())
                .status(request.getStatus())
                .build();

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public LessonResponse update(Long id, LessonRequest request) {
        Lesson lesson = getOrThrow(id);
        Level level = getLevelOrThrow(request.getLevelId());

        lesson.setLevel(level);
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setThumbnailUrl(request.getThumbnailUrl());
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setStatus(request.getStatus());

        return lessonMapper.toResponse(lessonRepository.save(lesson));
    }

    @Transactional
    public void delete(Long id) {
        Lesson lesson = getOrThrow(id);
        lessonRepository.delete(lesson);
    }

    private Lesson getOrThrow(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + id));
    }

    private Level getLevelOrThrow(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found: " + levelId));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
