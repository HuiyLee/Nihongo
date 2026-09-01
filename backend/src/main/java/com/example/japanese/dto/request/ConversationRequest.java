package com.example.japanese.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Requirements section 38, Phase 7 - AI conversation practice. Deliberately
 * stateless: the client holds the running conversation (in memory only, no
 * new entity/migration needed - matches "do not build everything at once")
 * and resends the full history with each turn.
 */
@Getter
@Setter
public class ConversationRequest {

    /** JLPT level code (N5-N1) to pitch vocabulary/grammar at. Defaults to N5. */
    private String level;

    @NotEmpty(message = "messages must not be empty")
    @Size(max = 40, message = "messages must contain at most 40 entries")
    @Valid
    private List<ConversationMessageDto> messages;
}
