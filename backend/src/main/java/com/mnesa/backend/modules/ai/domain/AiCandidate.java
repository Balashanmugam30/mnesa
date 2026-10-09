package com.mnesa.backend.modules.ai.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "extraction_id", nullable = false)
    private UUID extractionId;

    @Column(name = "candidate_index", nullable = false)
    @Builder.Default
    private Integer candidateIndex = 0;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String organization;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String category = "OTHER";

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "deadline_at")
    private Instant deadlineAt;

    @Column(name = "deadline_raw", length = 255)
    private String deadlineRaw;

    @Column(name = "confidence_score", nullable = false, precision = 4, scale = 3)
    @Builder.Default
    private BigDecimal confidenceScore = BigDecimal.valueOf(0.80);

    @Column(name = "evidence_snippets", columnDefinition = "TEXT")
    private String evidenceSnippets;

    @Column(name = "is_confirmed", nullable = false)
    @Builder.Default
    private boolean isConfirmed = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
