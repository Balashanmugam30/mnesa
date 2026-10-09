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
public class AssistantQueryResponseDto {

    private String answer;
    private String intent;

    @JsonProperty("cited_opportunity_ids")
    @Builder.Default
    private List<String> citedOpportunityIds = new ArrayList<>();

    @JsonProperty("action_suggestions")
    @Builder.Default
    private List<String> actionSuggestions = new ArrayList<>();

    @JsonProperty("latency_ms")
    private Double latencyMs;
}
