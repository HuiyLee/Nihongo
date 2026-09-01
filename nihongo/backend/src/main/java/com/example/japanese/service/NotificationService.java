package com.example.japanese.service;

import com.example.japanese.dto.response.NotificationResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.Notification;
import com.example.japanese.entity.NotificationType;
import com.example.japanese.entity.User;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.NotificationRepository;
import com.example.japanese.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Requirements section 24. notifyAllUsers() fans a notification out to
 * every registered user - called only from LessonService/ExamService on a
 * DRAFT->PUBLISHED status transition (never on every save of already-
 * published content, to avoid spamming users with routine edits).
 *
 * <p>A scheduled job that auto-generates VOCABULARY_REVIEW / STREAK
 * notifications (from nextReviewAt / streak milestones) is deliberately
 * not built in this phase - see NotificationType.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository.findByUserIdOrderByIdDesc(userId, pageable);
        return PageResponse.of(page, NotificationService::toResponse);
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markRead(Long userId, Long id) {
        Notification notification = notificationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void markAllRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    /** Fans one notification out to every user - see the class Javadoc for when this is (and isn't) called. */
    @Transactional
    public void notifyAllUsers(NotificationType type, String title, String content) {
        List<User> users = userRepository.findAll();
        List<Notification> notifications = users.stream()
                .map(user -> Notification.builder()
                        .user(user)
                        .title(title)
                        .content(content)
                        .type(type)
                        .read(false)
                        .build())
                .toList();
        notificationRepository.saveAll(notifications);
    }

    private static NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
