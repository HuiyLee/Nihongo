package com.example.japanese.controller;

import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.UserResponse;
import com.example.japanese.security.UserPrincipal;
import com.example.japanese.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** Returns the profile of the currently authenticated user (ownership is derived from the JWT, never from a URL param). */
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(userService.getByUsername(principal.getUsername()));
    }
}
