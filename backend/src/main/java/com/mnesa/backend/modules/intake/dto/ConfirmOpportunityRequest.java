package com.mnesa.backend.modules.intake.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmOpportunityRequest {

    private String title;
    private String organization;
    private String category;

    @JsonProperty("deadline_at")
    private Instant deadlineAt;

    private String notes;
}
