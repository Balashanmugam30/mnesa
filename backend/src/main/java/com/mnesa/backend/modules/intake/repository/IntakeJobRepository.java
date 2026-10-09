package com.mnesa.backend.modules.intake.repository;

import com.mnesa.backend.modules.intake.domain.IntakeJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntakeJobRepository extends JpaRepository<IntakeJob, UUID> {

    Optional<IntakeJob> findByCaptureId(UUID captureId);

    List<IntakeJob> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
