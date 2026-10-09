package com.mnesa.backend.modules.reminder.domain;

import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity logging all dispatched notification attempts and user engagement.
 */
@Entity
@Table(name = "notification_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id")
    private Reminder reminder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    @Builder.Default
    private NotificationChannel channel = NotificationChannel.PUSH;

    @Column(name = "provider", nullable = false)
    @Builder.Default
    private String provider = "MOCK_DEVELOPMENT";

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    @Builder.Default
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "attempt_count", nullable = false)
    @Builder.Default
    private int attemptCount = 0;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    @Column(name = "deep_link_uri")
    private String deepLinkUri;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "opened_at")
    private Instant openedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void markDelivered() {
        this.deliveryStatus = DeliveryStatus.DELIVERED;
        this.lastAttemptAt = Instant.now();
        this.attemptCount++;
    }

    public void markFailed(String error) {
        this.deliveryStatus = DeliveryStatus.FAILED;
        this.lastAttemptAt = Instant.now();
        this.lastError = error;
        this.attemptCount++;
    }

    public void markOpened() {
        this.openedAt = Instant.now();
        this.deliveryStatus = DeliveryStatus.OPENED;
    }
}
