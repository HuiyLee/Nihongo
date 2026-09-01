package com.example.japanese.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Requirements section 14.3. Owned exclusively by an {@link Exercise} (see
 * its {@code answers} collection) - there is no standalone answer CRUD
 * endpoint. {@code correct} backs the "isCorrect" field from the spec; it
 * must never be exposed to a learner before they submit (see
 * ExerciseAnswerResponse vs AdminExerciseAnswerResponse).
 */
@Getter
@Setter
@Entity
@Table(name = "exercise_answers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ExerciseAnswer extends BaseEntity {

    @Column(name = "answer_text", nullable = false, columnDefinition = "text")
    private String answerText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;
}
