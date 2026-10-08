package com.mnesa.backend.modules.auth.repository;

import com.mnesa.backend.modules.auth.domain.AuthIdentity;
import com.mnesa.backend.modules.auth.domain.IdentityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, UUID> {

    Optional<AuthIdentity> findByIdentityTypeAndIdentifier(IdentityType identityType, String identifier);

    List<AuthIdentity> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
