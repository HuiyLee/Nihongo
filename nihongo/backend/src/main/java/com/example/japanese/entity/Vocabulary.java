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

/** Requirements section 9. Always tied to a Level; the owning Lesson is optional. */
@Getter
@Setter
@Entity
@Table(name = "vocabularies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Vocabulary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "word", nullable = false, length = 255)
    private String word;

    @Column(name = "kanji", length = 255)
    private String kanji;

    @Column(name = "hiragana", length = 255)
    private String hiragana;

    @Column(name = "katakana", length = 255)
    private String katakana;

    @Column(name = "romaji", length = 255)
    private String romaji;

    @Column(name = "meaning", nullable = false, columnDefinition = "text")
    private String meaning;

    @Column(name = "part_of_speech", length = 100)
    private String partOfSpeech;

    @Column(name = "example", columnDefinition = "text")
    private String example;

    @Column(name = "example_meaning", columnDefinition = "text")
    private String exampleMeaning;

    @Column(name = "audio_url", length = 512)
    private String audioUrl;

    @Column(name = "image_url", length = 512)
    private String imageUrl;
}
