package com.example.japanese.controller.admin;

import com.example.japanese.dto.request.LevelRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.LevelResponse;
import com.example.japanese.service.LevelService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** ROLE_ADMIN only - enforced globally in SecurityConfig for /api/admin/**. */
@RestController
@RequestMapping("/api/admin/levels")
@RequiredArgsConstructor
public class AdminLevelController {

    private final LevelService levelService;

    @GetMapping
    public ApiResponse<List<LevelResponse>> list() {
        return ApiResponse.success(levelService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<LevelResponse> get(@PathVariable Long id) {
        return ApiResponse.success(levelService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LevelResponse>> create(@Valid @RequestBody LevelRequest request) {
        LevelResponse response = levelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Level created", response));
    }

    @PutMapping("/{id}")
    public ApiResponse<LevelResponse> update(@PathVariable Long id, @Valid @RequestBody LevelRequest request) {
        return ApiResponse.success("Level updated", levelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        levelService.delete(id);
        return ApiResponse.success("Level deleted", null);
    }
}
