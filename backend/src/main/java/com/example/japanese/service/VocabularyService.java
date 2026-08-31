package com.example.japanese.service;

import com.example.japanese.dto.request.VocabularyRequest;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.VocabularyResponse;
import com.example.japanese.entity.Lesson;
import com.example.japanese.entity.Level;
import com.example.japanese.entity.Vocabulary;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.mapper.VocabularyMapper;
import com.example.japanese.repository.LessonRepository;
import com.example.japanese.repository.LevelRepository;
import com.example.japanese.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VocabularyService {

    private final VocabularyRepository vocabularyRepository;
    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;
    private final VocabularyMapper vocabularyMapper;

    @Transactional(readOnly = true)
    public PageResponse<VocabularyResponse> search(String keyword, Long levelId, Long lessonId, Pageable pageable) {
        Page<Vocabulary> page = vocabularyRepository.search(blankToNull(keyword), levelId, lessonId, pageable);
        return PageResponse.of(page, vocabularyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VocabularyResponse findById(Long id) {
        return vocabularyMapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public VocabularyResponse create(VocabularyRequest request) {
        Vocabulary vocabulary = Vocabulary.builder()
                .lesson(resolveLesson(request.getLessonId()))
                .level(getLevelOrThrow(request.getLevelId()))
                .word(request.getWord())
                .kanji(request.getKanji())
                .hiragana(request.getHiragana())
                .katakana(request.getKatakana())
                .romaji(request.getRomaji())
                .meaning(request.getMeaning())
                .partOfSpeech(request.getPartOfSpeech())
                .example(request.getExample())
                .exampleMeaning(request.getExampleMeaning())
                .audioUrl(request.getAudioUrl())
                .imageUrl(request.getImageUrl())
                .build();

        return vocabularyMapper.toResponse(vocabularyRepository.save(vocabulary));
    }

    @Transactional
    public VocabularyResponse update(Long id, VocabularyRequest request) {
        Vocabulary vocabulary = getOrThrow(id);

        vocabulary.setLesson(resolveLesson(request.getLessonId()));
        vocabulary.setLevel(getLevelOrThrow(request.getLevelId()));
        vocabulary.setWord(request.getWord());
        vocabulary.setKanji(request.getKanji());
        vocabulary.setHiragana(request.getHiragana());
        vocabulary.setKatakana(request.getKatakana());
        vocabulary.setRomaji(request.getRomaji());
        vocabulary.setMeaning(request.getMeaning());
        vocabulary.setPartOfSpeech(request.getPartOfSpeech());
        vocabulary.setExample(request.getExample());
        vocabulary.setExampleMeaning(request.getExampleMeaning());
        vocabulary.setAudioUrl(request.getAudioUrl());
        vocabulary.setImageUrl(request.getImageUrl());

        return vocabularyMapper.toResponse(vocabularyRepository.save(vocabulary));
    }

    @Transactional
    public void delete(Long id) {
        vocabularyRepository.delete(getOrThrow(id));
    }

    private Vocabulary getOrThrow(Long id) {
        return vocabularyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vocabulary not found: " + id));
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
