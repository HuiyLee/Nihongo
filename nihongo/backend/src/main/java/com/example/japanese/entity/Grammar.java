package com.example.japanese.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/** Requirements section 13. */
@Getter
@Setter
@Entity
@Table(name = "grammars")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Grammar extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "pattern", nullable = false, length = 255)
    private String pattern;

    @Column(name = "meaning", nullable = false, columnDefinition = "text")
    private String meaning;

    @Column(name = "formation", columnDefinition = "text")
    private String formation;

    @Column(name = "explanation", columnDefinition = "text")
    private String explanation;

    @Column(name = "example", columnDefinition = "text")
    private String example;

    @Column(name = "example_meaning", columnDefinition = "text")
    private String exampleMeaning;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;
}
