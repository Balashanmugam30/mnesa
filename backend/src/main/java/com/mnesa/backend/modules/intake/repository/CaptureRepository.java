package com.mnesa.backend.modules.intake.repository;

import com.mnesa.backend.modules.intake.domain.Capture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaptureRepository extends JpaRepository<Capture, UUID> {

    Optional<Capture> findByUserIdAndIdempotencyKey(UUID userId, String idempotencyKey);

    Optional<Capture> findFirstByUserIdAndCanonicalUrlOrderByCreatedAtDesc(UUID userId, String canonicalUrl);

    List<Capture> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
