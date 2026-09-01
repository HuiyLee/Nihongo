package com.example.japanese.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Requirements section 16. Unlike Lesson/Exam, the spec's field list for
 * Reading has no status column, so there's no ContentStatus/draft-visibility
 * split here - every row is publicly browsable as soon as it's created,
 * same as Vocabulary/Kanji/Grammar. Furigana is plain <ruby>/<rt> markup
 * inside `content`, toggled client-side - no schema support needed.
 * `difficulty` reuses ExerciseDifficulty rather than a new enum.
 */
@Getter
@Setter
@Entity
@Table(name = "readings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Reading extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    /** Only surfaced to a user once they've completed this passage - see ReadingService. */
    @Column(name = "translation", columnDefinition = "text")
    private String translation;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private ExerciseDifficulty difficulty;
}
