package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.ExamRequest;
import com.example.japanese.dto.response.AdminExamResponse;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.PageResponse;
import com.example.japanese.entity.ContentStatus;
import com.example.japanese.service.ExamService;
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

/** Requirements section 17. Admin responses nest AdminExamQuestionResponse, whose exercise carries isCorrect. */
@RestController
@RequestMapping("/api/admin/exams")
@RequiredArgsConstructor
public class AdminExamController {

    private final ExamService examService;

    @GetMapping
    public ApiResponse<PageResponse<AdminExamResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long levelId,
            @RequestParam(required = false) ContentStatus status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    ) {
        return ApiResponse.success(
                examService.searchForAdmin(keyword, levelId, status, PageableFactory.build(page, size, sort))
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<AdminExamResponse> get(@PathVariable Long id) {
        return ApiResponse.success(examService.findByIdForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminExamResponse>> create(@Valid @RequestBody ExamRequest request) {
        AdminExamResponse response = examService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Exam created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<AdminExamResponse> update(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return ApiResponse.success("Exam updated", examService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return ApiResponse.success("Exam deleted", null);
    }
}
