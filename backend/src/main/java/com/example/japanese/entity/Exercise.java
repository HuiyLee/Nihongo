package com.example.japanese.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Requirements section 14.2. Always tied to a Level; the owning Lesson is optional (same pattern as Vocabulary/Kanji/Grammar). */
@Getter
@Setter
@Entity
@Table(name = "exercises")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Exercise extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ExerciseType type;

    @Column(name = "question", nullable = false, columnDefinition = "text")
    private String question;

    @Column(name = "explanation", columnDefinition = "text")
    private String explanation;

    @Column(name = "audio_url", length = 512)
    private String audioUrl;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private ExerciseDifficulty difficulty;

    /**
     * Owned collection - cascades saves/updates/deletes. The service always
     * replaces this list wholesale on update (clear + re-add) rather than
     * diffing, which is what makes orphanRemoval safe and simple here.
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "exercise_id", nullable = false)
    @OrderBy("orderIndex asc")
    private List<ExerciseAnswer> answers = new ArrayList<>();
}
