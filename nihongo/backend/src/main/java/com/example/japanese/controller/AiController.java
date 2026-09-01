package com.example.japanese.controller;

import com.example.japanese.dto.request.ConversationRequest;
import com.example.japanese.dto.request.GrammarExplanationRequest;
import com.example.japanese.dto.request.WritingCorrectionRequest;
import com.example.japanese.dto.response.ApiResponse;
import com.example.japanese.dto.response.ConversationResponse;
import com.example.japanese.dto.response.GrammarExplanationResponse;
import com.example.japanese.dto.response.WritingCorrectionResponse;
import com.example.japanese.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Requirements section 38, Phase 7 (optional AI features). No admin-only
 * restriction here - every endpoint just needs an authenticated user, which
 * SecurityConfig's default anyRequest().authenticated() rule already covers.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/grammar-explanation")
    public ApiResponse<GrammarExplanationResponse> explainGrammar(
            @Valid @RequestBody GrammarExplanationRequest request
    ) {
        return ApiResponse.success(aiService.explainGrammar(request));
    }

    @PostMapping("/writing-correction")
    public ApiResponse<WritingCorrectionResponse> correctWriting(
            @Valid @RequestBody WritingCorrectionRequest request
    ) {
        return ApiResponse.success(aiService.correctWriting(request));
    }

    @PostMapping("/conversation")
    public ApiResponse<ConversationResponse> converse(@Valid @RequestBody ConversationRequest request) {
        return ApiResponse.success(aiService.converse(request));
    }
}
