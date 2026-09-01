package com.example.japanese.entity;

/**
 * Requirements section 24. VOCABULARY_REVIEW and STREAK are reserved for a
 * future scheduled job (not built in this phase - see NotificationService)
 * so the data model doesn't need another migration once that job exists.
 * Only NEW_LESSON and NEW_EXAM are actually fired, from LessonService/
 * ExamService on a DRAFT->PUBLISHED transition.
 */
public enum NotificationType {
    NEW_LESSON,
    NEW_EXAM,
    VOCABULARY_REVIEW,
    STREAK,
    SYSTEM
}
