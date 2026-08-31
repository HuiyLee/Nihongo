package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** One question's submitted selection within a SubmitExamRequest. */
@Getter
@Setter
public class ExamAnswerSubmission {

    @NotNull(message = "examQuestionId is required")
    private Long examQuestionId;

    @NotEmpty(message = "answerIds is required")
    private List<Long> answerIds;
}
