package com.mnesa.backend.modules.opportunity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
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

    @Column(name = "source_url", columnDefinition = "TEXT")
    private String sourceUrl;

    @Column(name = "raw_content", columnDefinition = "TEXT")
    private String rawContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private OpportunityStatus status = OpportunityStatus.CAPTURED;

    @Column(name = "deadline_at")
    private Instant deadlineAt;

    @Column(name = "confidence_score", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal confidenceScore = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
