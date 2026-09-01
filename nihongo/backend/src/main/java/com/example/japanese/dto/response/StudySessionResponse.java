package com.example.japanese.dto.response;

import com.example.japanese.entity.StudyActivityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySessionResponse {
    private Long id;
    private StudyActivityType activityType;
    private Long referenceId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer durationSeconds;
}
