package com.mnesa.backend.modules.device.domain;

import com.mnesa.backend.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "device_installations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceInstallation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "installation_id", nullable = false, unique = true)
    private String installationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DevicePlatform platform = DevicePlatform.ANDROID;

    @Column(name = "push_token", length = 512)
    private String pushToken;

    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Column(name = "device_model", length = 100)
    private String deviceModel;

    @Column(name = "os_version", length = 50)
    private String osVersion;

    @Column(name = "last_active_at", nullable = false)
    @Builder.Default
    private Instant lastActiveAt = Instant.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
