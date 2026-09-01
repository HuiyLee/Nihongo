package com.example.japanese.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationMessageDto {

    @NotBlank(message = "role is required")
    @Pattern(regexp = "user|assistant", message = "role must be 'user' or 'assistant'")
    private String role;

    @NotBlank(message = "content is required")
    @Size(max = 2000, message = "content must be at most 2000 characters")
    private String content;
}
