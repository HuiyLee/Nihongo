package com.example.japanese.exception;

/**
 * Reserved for Phase 5 (JLPT exams) - included now so the exception taxonomy
 * matches requirements section 32 from the start.
 */
public class ExamExpiredException extends RuntimeException {
    public ExamExpiredException(String message) {
        super(message);
    }
}
