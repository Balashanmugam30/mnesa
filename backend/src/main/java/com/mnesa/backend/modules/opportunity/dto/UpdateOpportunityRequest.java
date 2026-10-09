package com.mnesa.backend.modules.opportunity.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOpportunityRequest {

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Organization must not exceed 255 characters")
    private String organization;

    private String category;

    private String description;

    private String sourceUrl;

    private String registrationUrl;

    private Instant deadlineAt;

    private String deadlineTimezone;

    private String eligibility;

    private String location;

    private String workMode;

    private String estimatedEffort;

    private String priority;

    private String notes;

    private List<String> tags;
}
