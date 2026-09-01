package com.example.japanese.service;

import com.example.japanese.dto.request.ReadingRequest;
import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.ReadingResponse;
import com.example.japanese.entity.Level;
import com.example.japanese.entity.Reading;
import com.example.japanese.entity.StudyActivityType;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.ReadingMapper;
import com.example.japanese.repository.LevelRepository;
import com.example.japanese.repository.ReadingRepository;
import com.example.japanese.repository.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Requirements section 16. No draft/published status - every row is
 * publicly browsable, so unlike ExamService/LessonService there's no
 * separate admin/public search method, only a revealTranslation flag on
 * the response mapping. complete() reuses StudySessionService (the same
 * endpoint backing POST /api/study-sessions) rather than writing its own
 * StudySession row, so "how a session gets recorded" stays in one place.
 */
@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ReadingRepository readingRepository;
    private final LevelRepository levelRepository;
    private final StudySessionRepository studySessionRepository;
    private final StudySessionService studySessionService;
    private final ReadingMapper readingMapper;

    @Transactional(readOnly = true)
    public PageResponse<ReadingResponse> search(String keyword, Long levelId, Pageable pageable) {
        Page<Reading> page = readingRepository.search(blankToNull(keyword), levelId, pageable);
        return PageResponse.of(page, r -> readingMapper.toResponse(r, false, null));
    }

    /** Admin: full CRUD detail, translation always visible, no completion concept. */
    @Transactional(readOnly = true)
    public ReadingResponse findById(Long id) {
        return readingMapper.toResponse(getOrThrow(id), true, null);
    }

    /** Public: translation only revealed once the caller has completed this passage. */
    @Transactional(readOnly = true)
    public ReadingResponse findById(Long userId, Long id) {
        Reading reading = getOrThrow(id);
        boolean completed = hasCompleted(userId, id);
        return readingMapper.toResponse(reading, completed, completed);
    }

    @Transactional
    public void complete(Long userId, Long id) {
        getOrThrow(id);
        if (hasCompleted(userId, id)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        StudySessionRequest session = new StudySessionRequest();
        session.setActivityType(StudyActivityType.READING);
        session.setReferenceId(id);
        session.setStartedAt(now);
        session.setEndedAt(now);
        studySessionService.record(userId, session);
    }

    @Transactional
    public ReadingResponse create(ReadingRequest request) {
        Reading reading = Reading.builder()
                .level(getLevelOrThrow(request.getLevelId()))
                .title(request.getTitle())
                .content(request.getContent())
                .translation(request.getTranslation())
                .difficulty(request.getDifficulty())
                .build();

        return readingMapper.toResponse(readingRepository.save(reading), true, null);
    }

    @Transactional
    public ReadingResponse update(Long id, ReadingRequest request) {
        Reading reading = getOrThrow(id);

        reading.setLevel(getLevelOrThrow(request.getLevelId()));
        reading.setTitle(request.getTitle());
        reading.setContent(request.getContent());
        reading.setTranslation(request.getTranslation());
        reading.setDifficulty(request.getDifficulty());

        return readingMapper.toResponse(readingRepository.save(reading), true, null);
    }

    @Transactional
    public void delete(Long id) {
        readingRepository.delete(getOrThrow(id));
    }

    private boolean hasCompleted(Long userId, Long readingId) {
        return studySessionRepository.existsByUserIdAndActivityTypeAndReferenceId(
                userId, StudyActivityType.READING, readingId
        );
    }

    private Reading getOrThrow(Long id) {
        return readingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reading not found: " + id));
    }

    private Level getLevelOrThrow(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found: " + levelId));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
