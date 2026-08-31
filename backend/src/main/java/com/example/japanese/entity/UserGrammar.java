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

/** See {@link UserVocabulary} for why this has no Lombok builder. */
@Getter
@Setter
@Entity
@Table(name = "user_grammar", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "grammar_id"}))
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserGrammar extends UserLearningItem {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grammar_id", nullable = false)
    private Grammar grammar;
}
