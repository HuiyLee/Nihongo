package com.example.japanese.service;

import com.example.japanese.dto.request.KanjiRequest;
import com.example.japanese.dto.response.KanjiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.Kanji;
import com.example.japanese.entity.Lesson;
import com.example.japanese.entity.Level;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.KanjiMapper;
import com.example.japanese.repository.KanjiRepository;
import com.example.japanese.repository.LessonRepository;
import com.example.japanese.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KanjiService {

    private final KanjiRepository kanjiRepository;
    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;
    private final KanjiMapper kanjiMapper;

    @Transactional(readOnly = true)
    public PageResponse<KanjiResponse> search(String keyword, Long levelId, Long lessonId, Pageable pageable) {
        Page<Kanji> page = kanjiRepository.search(blankToNull(keyword), levelId, lessonId, pageable);
        return PageResponse.of(page, kanjiMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public KanjiResponse findById(Long id) {
        return kanjiMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public KanjiResponse create(KanjiRequest request) {
        Kanji kanji = Kanji.builder()
                .lesson(resolveLesson(request.getLessonId()))
                .level(getLevelOrThrow(request.getLevelId()))
                .character(request.getCharacter())
                .meaning(request.getMeaning())
                .onyomi(request.getOnyomi())
                .kunyomi(request.getKunyomi())
                .strokeCount(request.getStrokeCount())
                .strokeOrderImageUrl(request.getStrokeOrderImageUrl())
                .example(request.getExample())
                .exampleMeaning(request.getExampleMeaning())
                .audioUrl(request.getAudioUrl())
                .build();

        return kanjiMapper.toResponse(kanjiRepository.save(kanji));
    }

    @Transactional
    public KanjiResponse update(Long id, KanjiRequest request) {
        Kanji kanji = getOrThrow(id);

        kanji.setLesson(resolveLesson(request.getLessonId()));
        kanji.setLevel(getLevelOrThrow(request.getLevelId()));
        kanji.setCharacter(request.getCharacter());
        kanji.setMeaning(request.getMeaning());
        kanji.setOnyomi(request.getOnyomi());
        kanji.setKunyomi(request.getKunyomi());
        kanji.setStrokeCount(request.getStrokeCount());
        kanji.setStrokeOrderImageUrl(request.getStrokeOrderImageUrl());
        kanji.setExample(request.getExample());
        kanji.setExampleMeaning(request.getExampleMeaning());
        kanji.setAudioUrl(request.getAudioUrl());

        return kanjiMapper.toResponse(kanjiRepository.save(kanji));
    }

    @Transactional
    public void delete(Long id) {
        kanjiRepository.delete(getOrThrow(id));
    }

    private Kanji getOrThrow(Long id) {
        return kanjiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kanji not found: " + id));
    }

    private Level getLevelOrThrow(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found: " + levelId));
    }

    private Lesson resolveLesson(Long lessonId) {
        if (lessonId == null) {
            return null;
        }
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found: " + lessonId));
    }

    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
