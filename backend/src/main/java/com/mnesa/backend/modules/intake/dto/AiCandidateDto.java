package com.mnesa.backend.modules.intake.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCandidateDto {

    private UUID id;

    @JsonProperty("candidate_index")
    private int candidateIndex;

    private String title;
    private String organization;
    private String category;
    private String summary;

    @JsonProperty("deadline_at")
    private Instant deadlineAt;

    @JsonProperty("confidence_score")
    private BigDecimal confidenceScore;

    @JsonProperty("is_confirmed")
    private boolean isConfirmed;
}
