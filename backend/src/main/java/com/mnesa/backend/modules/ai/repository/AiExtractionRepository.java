package com.mnesa.backend.modules.ai.repository;

import com.mnesa.backend.modules.ai.domain.AiExtraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiExtractionRepository extends JpaRepository<AiExtraction, UUID> {

    Optional<AiExtraction> findByIntakeJobId(UUID intakeJobId);

    Optional<AiExtraction> findByUserIdAndIntakeJobId(UUID userId, UUID intakeJobId);

    Optional<AiExtraction> findByCaptureId(UUID captureId);

    List<AiExtraction> findAllByUserId(UUID userId);
}
