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
public class OpportunitySummaryResponse {

    private UUID id;
    private String title;
    private String organization;
    private String category;
    private String status;
    private Instant deadlineAt;
    private String priority;
    private String location;
    private BigDecimal confidenceScore;
    private List<String> tags;
    private String urgencyLabel;
    private Instant createdAt;
}
