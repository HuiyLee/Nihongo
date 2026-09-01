package com.example.japanese.entity;

/**
 * Requirements section 14.1. All six types the data model supports are
 * declared here; Phase 4 only builds admin/learner UI + grading for
 * MULTIPLE_CHOICE, MULTIPLE_ANSWER, TRUE_FALSE, and FILL_IN_BLANK (all
 * graded the same way - see ExerciseService). MATCHING and LISTENING exist
 * as a valid value now so later phases don't need a schema change.
 */
public enum ExerciseType {
    MULTIPLE_CHOICE,
    MULTIPLE_ANSWER,
    TRUE_FALSE,
    FILL_IN_BLANK,
    MATCHING,
    LISTENING
}
