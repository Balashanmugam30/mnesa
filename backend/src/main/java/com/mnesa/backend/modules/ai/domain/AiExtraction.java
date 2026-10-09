package com.mnesa.backend.modules.ai.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ai_extractions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiExtraction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "capture_id", nullable = false)
    private UUID captureId;

    @Column(name = "intake_job_id", nullable = false, unique = true)
    private UUID intakeJobId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "schema_version", nullable = false, length = 20)
    @Builder.Default
    private String schemaVersion = "v1";

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false, length = 50)
    private ValidationStatus validationStatus;

    @Column(name = "overall_confidence", nullable = false, precision = 4, scale = 3)
    private BigDecimal overallConfidence;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String organization;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "deadline_at")
    private Instant deadlineAt;

    @Column(name = "deadline_raw", length = 255)
    private String deadlineRaw;

    @Column(name = "deadline_timezone", length = 50)
    private String deadlineTimezone;

    @Column(name = "deadline_ambiguous", nullable = false)
    @Builder.Default
    private boolean deadlineAmbiguous = false;

    @Column(name = "registration_url", length = 2048)
    private String registrationUrl;

    @Column(length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", length = 50)
    private WorkMode workMode;

    @Column(columnDefinition = "TEXT")
    private String eligibility;

    @Column(name = "estimated_effort_minutes")
    private Integer estimatedEffortMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private PriorityLevel priority = PriorityLevel.NORMAL;

    @Column(name = "priority_reason", length = 255)
    private String priorityReason;

    @Column(name = "extracted_fields", columnDefinition = "TEXT")
    private String extractedFields;

    @Column(name = "evidence_snippets", columnDefinition = "TEXT")
    private String evidenceSnippets;

    @Column(name = "warning_messages", columnDefinition = "TEXT")
    private String warningMessages;

    @Column(name = "error_category", length = 50)
    private String errorCategory;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "processing_duration_ms")
    private Long processingDurationMs;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
