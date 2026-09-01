package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Requirements section 35. passRate is a percentage (0-100) of COMPLETED exam attempts scoring >= 60. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {
    private long totalUsers;
    private long totalLessons;
    private long totalVocabulary;
    private long totalKanji;
    private long totalGrammar;
    private long totalExercises;
    private long totalExams;
    private long totalStudySessions;
    private long totalExamAttempts;
    private double passRate;
}
