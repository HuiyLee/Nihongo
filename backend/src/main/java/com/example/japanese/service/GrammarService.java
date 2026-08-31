package com.example.japanese.service;

import com.example.japanese.dto.request.GrammarRequest;
import com.example.japanese.dto.response.GrammarResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.Grammar;
import com.example.japanese.entity.Lesson;
import com.example.japanese.entity.Level;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.GrammarMapper;
import com.example.japanese.repository.GrammarRepository;
import com.example.japanese.repository.LessonRepository;
import com.example.japanese.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GrammarService {

    private final GrammarRepository grammarRepository;
    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;
    private final GrammarMapper grammarMapper;

    @Transactional(readOnly = true)
    public PageResponse<GrammarResponse> search(String keyword, Long levelId, Long lessonId, Pageable pageable) {
        Page<Grammar> page = grammarRepository.search(blankToNull(keyword), levelId, lessonId, pageable);
        return PageResponse.of(page, grammarMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public GrammarResponse findById(Long id) {
        return grammarMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public GrammarResponse create(GrammarRequest request) {
        Grammar grammar = Grammar.builder()
                .lesson(resolveLesson(request.getLessonId()))
                .level(getLevelOrThrow(request.getLevelId()))
                .pattern(request.getPattern())
                .meaning(request.getMeaning())
                .formation(request.getFormation())
                .explanation(request.getExplanation())
                .example(request.getExample())
                .exampleMeaning(request.getExampleMeaning())
                .notes(request.getNotes())
                .build();

        return grammarMapper.toResponse(grammarRepository.save(grammar));
    }

    @Transactional
    public GrammarResponse update(Long id, GrammarRequest request) {
        Grammar grammar = getOrThrow(id);

        grammar.setLesson(resolveLesson(request.getLessonId()));
        grammar.setLevel(getLevelOrThrow(request.getLevelId()));
        grammar.setPattern(request.getPattern());
        grammar.setMeaning(request.getMeaning());
        grammar.setFormation(request.getFormation());
        grammar.setExplanation(request.getExplanation());
        grammar.setExample(request.getExample());
        grammar.setExampleMeaning(request.getExampleMeaning());
        grammar.setNotes(request.getNotes());

        return grammarMapper.toResponse(grammarRepository.save(grammar));
    }

    @Transactional
    public void delete(Long id) {
        grammarRepository.delete(getOrThrow(id));
    }

    private Grammar getOrThrow(Long id) {
        return grammarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grammar not found: " + id));
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
