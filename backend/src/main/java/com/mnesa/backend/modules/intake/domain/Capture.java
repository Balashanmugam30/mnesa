package com.mnesa.backend.modules.intake.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "captures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capture {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 32)
    private SourceType sourceType;

    @Column(name = "original_text", columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "original_url", length = 2048)
    private String originalUrl;

    @Column(name = "canonical_url", length = 2048)
    private String canonicalUrl;

    @Column(name = "source_domain", length = 255)
    private String sourceDomain;

    @Column(name = "client_capture_id", length = 64)
    private String clientCaptureId;

    @Column(name = "idempotency_key", nullable = false, length = 128)
    private String idempotencyKey;

    @Column(name = "media_mime_type", length = 100)
    private String mediaMimeType;

    @Column(name = "media_size_bytes")
    private Long mediaSizeBytes;

    @Column(name = "media_storage_path", length = 1024)
    private String mediaStoragePath;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Builder.Default
    private CaptureStatus status = CaptureStatus.RECEIVED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
