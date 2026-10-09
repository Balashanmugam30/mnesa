package com.mnesa.backend.modules.opportunity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "opportunities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String organization;

    @Enumerated(EnumType.STRING)
    @Column(name = "opportunity_type", nullable = false, length = 50)
    @Builder.Default
    private OpportunityType opportunityType = OpportunityType.OTHER;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(name = "registration_url", length = 2048)
    private String registrationUrl;

    @Column(name = "source_domain", length = 255)
    private String sourceDomain;

    @Column(name = "raw_content", columnDefinition = "TEXT")
    private String rawContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private OpportunityStatus status = OpportunityStatus.SAVED;

    @Column(name = "previous_status", length = 50)
    private String previousStatus;

    @Column(name = "deadline_at")
    private Instant deadlineAt;

    @Column(name = "deadline_timezone", length = 50)
    private String deadlineTimezone;

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    @Column(length = 255)
    private String location;

    @Column(name = "work_mode", length = 50)
    @Builder.Default
    private String workMode = "UNSPECIFIED";

    @Column(name = "estimated_effort", length = 50)
    private String estimatedEffort;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String priority = "NORMAL";

    @Column(name = "priority_reason", length = 255)
    private String priorityReason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "confidence_score", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal confidenceScore = BigDecimal.ZERO;

    @Column(name = "last_status_change_at")
    @Builder.Default
    private Instant lastStatusChangeAt = Instant.now();

    @Column(name = "archived_at")
    private Instant archivedAt;

    @Column(name = "extraction_id")
    private UUID extractionId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "opportunity_tags",
            joinColumns = @JoinColumn(name = "opportunity_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
