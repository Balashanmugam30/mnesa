package com.mnesa.backend.modules.auth.domain;

import com.mnesa.backend.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_identities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_type", nullable = false)
    private IdentityType identityType;

    @Column(nullable = false)
    private String identifier;

    @Column(name = "credential_hash")
    private String credentialHash;

    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
