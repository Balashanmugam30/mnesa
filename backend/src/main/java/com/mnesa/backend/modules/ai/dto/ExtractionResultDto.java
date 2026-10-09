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
public class ExtractionResultDto {

    private boolean success;
    private ExtractedOpportunityDto opportunity;

    @JsonProperty("provider_used")
    private String providerUsed;

    @JsonProperty("latency_ms")
    private Double latencyMs;

    @JsonProperty("sanitization_flags")
    @Builder.Default
    private List<String> sanitizationFlags = new ArrayList<>();

    @JsonProperty("validation_status")
    private String validationStatus;

    @JsonProperty("error_message")
    private String errorMessage;
}
