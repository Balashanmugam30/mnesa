package com.mnesa.backend.modules.opportunity.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityResponse {

    private UUID id;
    private UUID userId;
    private String title;
    private String organization;
    private String category;
    private String description;
    private String sourceUrl;
    private String registrationUrl;
    private String sourceDomain;
    private String status;
    private String previousStatus;
    private Instant deadlineAt;
    private String deadlineTimezone;
    private String eligibility;
    private String location;
    private String workMode;
    private String estimatedEffort;
    private String priority;
    private String priorityReason;
    private String notes;
    private BigDecimal confidenceScore;
    private Instant lastStatusChangeAt;
    private Instant archivedAt;
    private UUID extractionId;
    private List<String> tags;
    private Instant createdAt;
    private Instant updatedAt;
}
