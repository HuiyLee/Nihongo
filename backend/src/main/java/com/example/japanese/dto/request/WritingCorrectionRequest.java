package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WritingCorrectionRequest {

    @NotBlank(message = "text is required")
    @Size(max = 2000, message = "text must be at most 2000 characters")
    private String text;
}
