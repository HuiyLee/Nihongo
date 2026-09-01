package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.NotificationResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.NotificationService;
import com.example.japanese.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Requirements section 24. Always scoped to the caller. */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                notificationService.list(principal.getId(), PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> unreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(Map.of("count", notificationService.unreadCount(principal.getId())));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markRead(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id
    ) {
        return ApiResponse.success(notificationService.markRead(principal.getId(), id));
    }

    @PostMapping("/read-all")
    public ApiResponse<Void> markAllRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllRead(principal.getId());
        return ApiResponse.success("All notifications marked as read", null);
    }
}
