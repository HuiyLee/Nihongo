package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.ReadingResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.ReadingService;
import com.example.japanese.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Requirements section 16.3. */
@RestController
@RequestMapping("/api/readings")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    @GetMapping
    public ApiResponse<PageResponse<ReadingResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                readingService.search(keyword, levelId, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ReadingResponse> get(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id
    ) {
        return ApiResponse.success(readingService.findById(principal.getId(), id));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<Void> complete(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id
    ) {
        readingService.complete(principal.getId(), id);
        return ApiResponse.success("Reading marked as complete", null);
    }
}
