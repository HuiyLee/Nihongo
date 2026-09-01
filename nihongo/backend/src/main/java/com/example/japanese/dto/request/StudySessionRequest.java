package com.example.japanese.dto.request;

import com.example.japanese.entity.StudyActivityType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudySessionRequest {

    @NotNull(message = "activityType is required")
    private StudyActivityType activityType;

    private Long referenceId;

    @NotNull(message = "startedAt is required")
    private LocalDateTime startedAt;

    @NotNull(message = "endedAt is required")
    private LocalDateTime endedAt;
}
