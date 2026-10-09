package com.mnesa.backend.modules.intake.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntakeRequest {

    @NotBlank(message = "Idempotency key is required")
    @Size(max = 128, message = "Idempotency key must not exceed 128 characters")
    @Schema(description = "Unique client transaction or idempotency key to prevent double captures", example = "capture-uuid-12345")
    private String idempotencyKey;

    @Schema(description = "Shared text or raw caption", example = "Google Summer Internship 2026. Apply before Dec 1.")
    private String text;

    @Size(max = 2048, message = "URL must not exceed 2048 characters")
    @Schema(description = "Shared target URL if isolated", example = "https://careers.google.com/jobs/results/12345")
    private String url;

    @Schema(description = "Optional client source classification hint (URL, TEXT, IMAGE, HYBRID)", example = "URL")
    private String sourceType;

    @Size(max = 64, message = "Client capture ID must not exceed 64 characters")
    @Schema(description = "Client-generated UUID for local Room tracking", example = "550e8400-e29b-41d4-a716-446655440000")
    private String clientCaptureId;

    @Schema(description = "MIME type if media was shared", example = "image/png")
    private String mediaMimeType;

    @Schema(description = "File size in bytes if media was shared", example = "1048576")
    private Long mediaSizeBytes;

    @Schema(description = "Optional client device/source metadata in JSON string format")
    private String metadata;

    @Schema(description = "Optional base64-encoded image payload for OCR extraction")
    private String imageBase64;
}

