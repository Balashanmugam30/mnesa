package com.mnesa.backend.modules.ai.repository;

import com.mnesa.backend.modules.ai.domain.AiCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiCandidateRepository extends JpaRepository<AiCandidate, UUID> {

    List<AiCandidate> findAllByExtractionIdOrderByCandidateIndexAsc(UUID extractionId);
}
