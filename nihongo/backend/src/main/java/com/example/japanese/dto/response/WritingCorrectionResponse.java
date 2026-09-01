package com.example.japanese.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WritingCorrectionResponse {

    private String original;
    private String corrected;
    private String feedback;
}
