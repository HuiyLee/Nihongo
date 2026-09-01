package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.ReadingRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.dto.response.ReadingResponse;
import com.example.japanese.service.ReadingService;
import com.example.japanese.util.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/readings")
@RequiredArgsConstructor
public class AdminReadingController {

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
    public ApiResponse<ReadingResponse> get(@PathVariable Long id) {
        return ApiResponse.success(readingService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReadingResponse>> create(@Valid @RequestBody ReadingRequest request) {
        ReadingResponse response = readingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Reading created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReadingResponse> update(@PathVariable Long id, @Valid @RequestBody ReadingRequest request) {
        return ApiResponse.success("Reading updated", readingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        readingService.delete(id);
        return ApiResponse.success("Reading deleted", null);
    }
}
