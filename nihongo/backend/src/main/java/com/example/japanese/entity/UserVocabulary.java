package com.example.japanese.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * No builder here on purpose: this class extends {@link UserLearningItem}
 * and plain Lombok {@code @Builder} does not include inherited fields
 * (user, status, correctCount, ...), which would silently drop them. The
 * service constructs instances with the no-args constructor + setters instead.
 */
@Getter
@Setter
@Entity
@Table(name = "user_vocabulary", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "vocabulary_id"}))
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserVocabulary extends UserLearningItem {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;
}
