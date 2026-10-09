package com.mnesa.backend.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtractedOpportunityDto {

    private FieldResultDto<String> title;
    private FieldResultDto<String> organization;
    private FieldResultDto<String> category;
    private String summary;
    private DeadlineDto deadline;
    private FieldResultDto<String> eligibility;
    private FieldResultDto<String> location;

    @JsonProperty("work_mode")
    private String workMode;

    @JsonProperty("registration_url")
    private String registrationUrl;

    @JsonProperty("source_url")
    private String sourceUrl;

    @JsonProperty("estimated_effort_minutes")
    private Integer estimatedEffortMinutes;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private String priority = "NORMAL";

    @JsonProperty("priority_reason")
    private String priorityReason;

    @JsonProperty("overall_confidence")
    @Builder.Default
    private Double overallConfidence = 0.0;

    @JsonProperty("validation_status")
    private String validationStatus;

    @JsonProperty("warning_messages")
    @Builder.Default
    private List<String> warningMessages = new ArrayList<>();

    @JsonProperty("evidence_snippets")
    @Builder.Default
    private List<String> evidenceSnippets = new ArrayList<>();
}
