package com.example.japanese.controller;

import com.example.japanese.dto.request.BookmarkRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.BookmarkResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.BookmarkTargetType;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.BookmarkService;
import com.example.japanese.util.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Requirements section 23. Every endpoint here operates only on the caller's
 * own bookmarks - the userId always comes from the authenticated principal,
 * never from a request parameter (section 33).
 */
@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ApiResponse<BookmarkResponse> create(
            @AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody BookmarkRequest request
    ) {
        return ApiResponse.success(bookmarkService.create(principal.getId(), request));
    }

    @GetMapping
    public ApiResponse<PageResponse<BookmarkResponse>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) BookmarkTargetType targetType,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                bookmarkService.list(principal.getId(), targetType, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/exists")
    public ApiResponse<Boolean> exists(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam BookmarkTargetType targetType,
            @RequestParam Long targetId
    ) {
        return ApiResponse.success(bookmarkService.exists(principal.getId(), targetType, targetId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        bookmarkService.delete(principal.getId(), id);
        return ApiResponse.success(null);
    }
}
