package com.example.japanese.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Requirements section 38, Phase 7 - AI grammar explanation. Provide
 * grammarId to ground the explanation in an existing Grammar entity, and/or
 * a free-text question for anything else. At least one must be present
 * (validated in AiService, since neither is individually required).
 */
@Getter
@Setter
public class GrammarExplanationRequest {

    private Long grammarId;

    @Size(max = 1000, message = "question must be at most 1000 characters")
    private String question;
}
