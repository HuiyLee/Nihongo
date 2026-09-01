package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreakResponse {
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastActiveDate;
}
