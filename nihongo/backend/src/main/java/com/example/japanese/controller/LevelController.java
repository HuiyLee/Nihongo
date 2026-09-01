package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.LevelResponse;
import com.example.japanese.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Authenticated USER/ADMIN read access - reference data used by filters across the app. */
@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping
    public ApiResponse<List<LevelResponse>> list() {
        return ApiResponse.success(levelService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<LevelResponse> get(@PathVariable Long id) {
        return ApiResponse.success(levelService.findById(id));
    }
}
