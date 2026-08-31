package com.example.japanese.entity;

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

/**
 * One row per answer option the learner selected for one exam question -
 * a MULTIPLE_ANSWER question naturally gets several rows sharing the same
 * examAttempt+examQuestion. Unlike ExerciseAnswer/ExamQuestion, this isn't
 * an "edited together with its parent in a form" collection - it's written
 * once at submit time - so it's a plain entity with its own repository
 * rather than a cascaded collection on ExamAttempt.
 */
@Getter
@Setter
@Entity
@Table(name = "exam_answers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ExamAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_attempt_id", nullable = false)
    private ExamAttempt examAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id", nullable = false)
    private ExamQuestion examQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id", nullable = false)
    private ExerciseAnswer selectedAnswer;
}
