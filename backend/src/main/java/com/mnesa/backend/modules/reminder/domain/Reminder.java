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
 * JPA entity representing a scheduled, snoozed, or dispatched opportunity reminder.
 */
@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_type", nullable = false)
    @Builder.Default
    private ReminderType reminderType = ReminderType.CUSTOM;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(name = "target_timezone", nullable = false)
    @Builder.Default
    private String targetTimezone = "UTC";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReminderStatus status = ReminderStatus.SCHEDULED;

    @Column(name = "snooze_until")
    private Instant snoozeUntil;

    @Column(name = "snooze_count", nullable = false)
    @Builder.Default
    private int snoozeCount = 0;

    @Column(name = "smart_reason")
    private String smartReason;

    @Column(name = "sent_at")
    private Instant sentAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public void snooze(Instant targetInstant) {
        if (targetInstant == null || targetInstant.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Snooze time must be in the future");
        }
        this.snoozeUntil = targetInstant;
        this.snoozeCount++;
        this.status = ReminderStatus.SNOOZED;
    }

    public void cancel() {
        this.status = ReminderStatus.CANCELLED;
    }

    public void dismiss() {
        this.status = ReminderStatus.DISMISSED;
    }

    public void markSent() {
        this.status = ReminderStatus.SENT;
        this.sentAt = Instant.now();
    }

    public void claim() {
        this.status = ReminderStatus.CLAIMED;
    }

    public Instant getEffectiveTriggerTime() {
        return snoozeUntil != null ? snoozeUntil : scheduledAt;
    }
}
