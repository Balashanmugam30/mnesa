package com.mnesa.backend.modules.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Convert(converter = StringListConverter.class)
    @Column(name = "interests")
    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_timing", nullable = false)
    @Builder.Default
    private ReminderTimingPreference reminderTiming = ReminderTimingPreference.STANDARD;

    @Column(name = "email_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean emailNotificationsEnabled = true;

    @Column(name = "push_notifications_enabled", nullable = false)
    @Builder.Default
    private boolean pushNotificationsEnabled = true;

    @Column(name = "timezone", nullable = false)
    @Builder.Default
    private String timezone = "UTC";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
