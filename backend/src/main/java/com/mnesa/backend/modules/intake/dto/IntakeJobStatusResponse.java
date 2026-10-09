package com.mnesa.backend.modules.intake.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntakeJobStatusResponse {

    @JsonProperty("job_id")
    private UUID jobId;

    @JsonProperty("capture_id")
    private UUID captureId;

    private String status;

    @JsonProperty("attempt_count")
    private int attemptCount;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    private AiExtractionDto extraction;
}
