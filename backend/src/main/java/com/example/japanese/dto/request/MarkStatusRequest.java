package com.example.japanese.dto.request;

import com.example.japanese.entity.MarkOutcome;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarkStatusRequest {

    @NotNull(message = "outcome is required")
    private MarkOutcome outcome;
}
