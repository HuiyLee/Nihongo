package com.example.japanese.controller.admin;

import com.example.japanese.dto.response.AdminStatsResponse;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Requirements section 35. */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping
    public ApiResponse<AdminStatsResponse> get() {
        return ApiResponse.success(adminStatsService.getStats());
    }
}
