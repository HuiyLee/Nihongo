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

/** Requirements section 12. */
@Getter
@Setter
@Entity
@Table(name = "kanjis")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Kanji extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Column(name = "character", nullable = false, length = 10)
    private String character;

    @Column(name = "meaning", nullable = false, columnDefinition = "text")
    private String meaning;

    @Column(name = "onyomi", length = 255)
    private String onyomi;

    @Column(name = "kunyomi", length = 255)
    private String kunyomi;

    @Column(name = "stroke_count")
    private Integer strokeCount;

    @Column(name = "stroke_order_image_url", length = 512)
    private String strokeOrderImageUrl;

    @Column(name = "example", columnDefinition = "text")
    private String example;

    @Column(name = "example_meaning", columnDefinition = "text")
    private String exampleMeaning;

    @Column(name = "audio_url", length = 512)
    private String audioUrl;
}
