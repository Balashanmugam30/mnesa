package com.mnesa.backend.modules.opportunity.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityActivityResponse {

    private UUID id;
    private UUID opportunityId;
    private String actionType;
    private String oldStatus;
    private String newStatus;
    private String description;
    private Instant createdAt;
}
