package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GrammarExplanationResponse {

    /** Echoes the grammar pattern when grammarId was provided, null otherwise. */
    private String pattern;

    private String explanation;
}
