package com.mnesa.backend.modules.assistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssistantQueryRequest {

    @NotBlank(message = "Question is required")
    @Size(min = 2, max = 1000, message = "Question must be between 2 and 1000 characters")
    @Schema(description = "Natural language question about your saved opportunities", example = "What deadlines do I have this week?")
    private String question;

    @Schema(description = "User local timezone for relative time interpretation", example = "UTC")
    @Builder.Default
    private String timezone = "UTC";
}
