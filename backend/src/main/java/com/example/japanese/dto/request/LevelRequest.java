package com.example.japanese.dto.request;

import com.example.japanese.entity.ContentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelRequest {

    @NotBlank(message = "code is required")
    @Size(max = 20, message = "code must be at most 20 characters")
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    private String description;

    @NotNull(message = "orderIndex is required")
    private Integer orderIndex;

    @NotNull(message = "status is required")
    private ContentStatus status;
}
