package com.mnesa.backend.modules.intake.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiExtractionDto {

    private UUID id;
    private String title;
    private String organization;
    private String category;
    private String summary;

    @JsonProperty("deadline_at")
    private Instant deadlineAt;

    @JsonProperty("deadline_raw")
    private String deadlineRaw;

    @JsonProperty("deadline_ambiguous")
    private boolean deadlineAmbiguous;

    @JsonProperty("registration_url")
    private String registrationUrl;

    private String location;

    @JsonProperty("work_mode")
    private String workMode;

    private String eligibility;

    @JsonProperty("estimated_effort_minutes")
    private Integer estimatedEffortMinutes;

    private String priority;

    @JsonProperty("priority_reason")
    private String priorityReason;

    @JsonProperty("overall_confidence")
    private BigDecimal overallConfidence;

    @JsonProperty("validation_status")
    private String validationStatus;

    @JsonProperty("evidence_snippets")
    @Builder.Default
    private List<String> evidenceSnippets = new ArrayList<>();

    @JsonProperty("warning_messages")
    @Builder.Default
    private List<String> warningMessages = new ArrayList<>();

    @JsonProperty("processing_duration_ms")
    private Long processingDurationMs;
}
