package com.mnesa.backend.modules.intake.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntakeResponse {

    @Schema(description = "Server-assigned capture UUID")
    private UUID captureId;

    @Schema(description = "Server-assigned asynchronous intake job UUID")
    private UUID jobId;

    @Schema(description = "Current intake status", example = "RECEIVED")
    private String status;

    @Schema(description = "Classified source type", example = "URL")
    private String sourceType;

    @Schema(description = "Sanitized canonical opportunity URL", example = "https://careers.google.com/jobs/results/12345")
    private String canonicalUrl;

    @Schema(description = "User-facing acknowledgment or status description", example = "Opportunity accepted for intelligent processing")
    private String message;

    @Schema(description = "True if this opportunity or idempotency key was previously captured", example = "false")
    private boolean duplicate;

    @Schema(description = "Capture timestamp")
    private Instant createdAt;
}
