package com.example.japanese.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** Requirements section 17.3 / 18. */
@Getter
@Setter
public class SubmitExamRequest {

    @NotEmpty(message = "answers is required")
    @Valid
    private List<ExamAnswerSubmission> answers;
}
