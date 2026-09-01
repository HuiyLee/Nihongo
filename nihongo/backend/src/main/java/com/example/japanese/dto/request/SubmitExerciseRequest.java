package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** Requirements section 14.4. */
@Getter
@Setter
public class SubmitExerciseRequest {

    @NotEmpty(message = "answerIds is required")
    private List<Long> answerIds;
}
