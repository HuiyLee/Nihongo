package com.example.japanese.service;

import com.example.japanese.dto.request.StudySessionRequest;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.StudySessionResponse;
import com.example.japanese.entity.StudySession;
import com.example.japanese.entity.User;
import com.example.japanese.exception.InvalidRequestException;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.StudySessionRepository;
import com.example.japanese.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/** Requirements section 21. A session is recorded in one shot once the activity is finished on the client. */
@Service
@RequiredArgsConstructor
public class StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public StudySessionResponse record(Long userId, StudySessionRequest request) {
        if (request.getEndedAt().isBefore(request.getStartedAt())) {
            throw new InvalidRequestException("endedAt must not be before startedAt");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        int durationSeconds = (int) Duration.between(request.getStartedAt(), request.getEndedAt()).getSeconds();

        StudySession session = StudySession.builder()
                .user(user)
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .durationSeconds(durationSeconds)
                .activityType(request.getActivityType())
                .referenceId(request.getReferenceId())
                .build();

        return toResponse(studySessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public PageResponse<StudySessionResponse> list(Long userId, Pageable pageable) {
        return PageResponse.of(studySessionRepository.findByUserId(userId, pageable), StudySessionService::toResponse);
    }

    private static StudySessionResponse toResponse(StudySession session) {
        return StudySessionResponse.builder()
                .id(session.getId())
                .activityType(session.getActivityType())
                .referenceId(session.getReferenceId())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .durationSeconds(session.getDurationSeconds())
                .build();
    }
}
