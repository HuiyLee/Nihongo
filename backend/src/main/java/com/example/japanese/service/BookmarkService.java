package com.example.japanese.service;

import com.example.japanese.dto.request.BookmarkRequest;
import com.example.japanese.dto.response.BookmarkResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.Bookmark;
import com.example.japanese.entity.BookmarkTargetType;
import com.example.japanese.entity.Grammar;
import com.example.japanese.entity.Kanji;
import com.example.japanese.entity.User;
import com.example.japanese.entity.Vocabulary;
import com.example.japanese.exception.DuplicateResourceException;
import com.example.japanese.exception.InvalidRequestException;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.BookmarkRepository;
import com.example.japanese.repository.GrammarRepository;
import com.example.japanese.repository.KanjiRepository;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.repository.VocabularyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Requirements section 23 / BR-011. targetId is validated against the real
 * Vocabulary/Kanji/Grammar tables before a bookmark is created; READING is
 * accepted by the enum (per the data model in section 23) but rejected here
 * since no Reading entity exists yet in this phase.
 */
@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final VocabularyRepository vocabularyRepository;
    private final KanjiRepository kanjiRepository;
    private final GrammarRepository grammarRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookmarkResponse create(Long userId, BookmarkRequest request) {
        BookmarkTargetType targetType = request.getTargetType();
        Long targetId = request.getTargetId();

        String displayText = resolveDisplayTextOrThrow(targetType, targetId);

        if (bookmarkRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId)) {
            throw new DuplicateResourceException("This item is already bookmarked");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .targetType(targetType)
                .targetId(targetId)
                .build();

        Bookmark saved = bookmarkRepository.save(bookmark);
        return toResponse(saved, displayText);
    }

    @Transactional(readOnly = true)
    public PageResponse<BookmarkResponse> list(Long userId, BookmarkTargetType targetType, Pageable pageable) {
        Page<Bookmark> page = targetType == null
                ? bookmarkRepository.findByUserId(userId, pageable)
                : bookmarkRepository.findByUserIdAndTargetType(userId, targetType, pageable);
        return PageResponse.of(page, b -> toResponse(b, resolveDisplayTextSafely(b.getTargetType(), b.getTargetId())));
    }

    @Transactional(readOnly = true)
    public boolean exists(Long userId, BookmarkTargetType targetType, Long targetId) {
        return bookmarkRepository.existsByUserIdAndTargetTypeAndTargetId(userId, targetType, targetId);
    }

    @Transactional
    public void delete(Long userId, Long bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findByIdAndUserId(bookmarkId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found: " + bookmarkId));
        bookmarkRepository.delete(bookmark);
    }

    /** Validates the target exists and returns a short label for it; throws if it doesn't. */
    private String resolveDisplayTextOrThrow(BookmarkTargetType targetType, Long targetId) {
        return switch (targetType) {
            case VOCABULARY -> {
                Vocabulary vocabulary = vocabularyRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Vocabulary not found: " + targetId));
                yield vocabulary.getWord();
            }
            case KANJI -> {
                Kanji kanji = kanjiRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Kanji not found: " + targetId));
                yield kanji.getCharacter();
            }
            case GRAMMAR -> {
                Grammar grammar = grammarRepository.findById(targetId)
                        .orElseThrow(() -> new ResourceNotFoundException("Grammar not found: " + targetId));
                yield grammar.getPattern();
            }
            case READING -> throw new InvalidRequestException("Bookmarking reading passages is not supported yet");
        };
    }

    /** Same lookup as above but tolerant of a target having since been deleted (used when listing). */
    private String resolveDisplayTextSafely(BookmarkTargetType targetType, Long targetId) {
        try {
            return switch (targetType) {
                case VOCABULARY -> vocabularyRepository.findById(targetId).map(Vocabulary::getWord).orElse(null);
                case KANJI -> kanjiRepository.findById(targetId).map(Kanji::getCharacter).orElse(null);
                case GRAMMAR -> grammarRepository.findById(targetId).map(Grammar::getPattern).orElse(null);
                case READING -> null;
            };
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static BookmarkResponse toResponse(Bookmark bookmark, String displayText) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .targetType(bookmark.getTargetType())
                .targetId(bookmark.getTargetId())
                .displayText(displayText)
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}
