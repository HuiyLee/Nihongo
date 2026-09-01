package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressOverviewResponse {
    private CategoryProgressResponse vocabulary;
    private CategoryProgressResponse kanji;
    private CategoryProgressResponse grammar;
    private CategoryProgressResponse lessons;
    private CategoryProgressResponse exams;
}
