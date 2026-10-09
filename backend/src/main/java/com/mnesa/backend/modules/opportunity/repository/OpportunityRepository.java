package com.mnesa.backend.modules.opportunity.repository;

import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {

    Optional<Opportunity> findByIdAndUserId(UUID id, UUID userId);

    List<Opportunity> findAllByUserId(UUID userId);
}
