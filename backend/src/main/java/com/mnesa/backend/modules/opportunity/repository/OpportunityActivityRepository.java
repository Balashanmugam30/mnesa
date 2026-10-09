package com.mnesa.backend.modules.opportunity.repository;

import com.mnesa.backend.modules.opportunity.domain.OpportunityActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OpportunityActivityRepository extends JpaRepository<OpportunityActivity, UUID> {

    List<OpportunityActivity> findAllByOpportunityIdAndUserIdOrderByCreatedAtDesc(UUID opportunityId, UUID userId);

    List<OpportunityActivity> findAllByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<OpportunityActivity> findAllByUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(UUID userId, java.time.Instant since);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM OpportunityActivity a WHERE a.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}

