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

/**
 * Requirements section 17.1. Reuses {@link ContentStatus} (DRAFT/PUBLISHED/
 * ARCHIVED) rather than a duplicate enum - it's the exact same three values
 * already used for {@link Lesson} (section 39: avoid duplicated code).
 */
@Getter
@Setter
@Entity
@Table(name = "exams")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Exam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    /** Always server-computed from {@code questions.size()} - never accepted from a request body. */
    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContentStatus status;

    /**
     * Owned collection - cascades saves/updates/deletes. The service always
     * replaces this list wholesale on update (clear + re-add), same as
     * Exercise's answers.
     */
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "exam_id", nullable = false)
    @OrderBy("orderIndex asc")
    private List<ExamQuestion> questions = new ArrayList<>();
}
