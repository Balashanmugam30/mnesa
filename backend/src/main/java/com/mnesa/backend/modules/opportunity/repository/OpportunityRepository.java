package com.mnesa.backend.modules.opportunity.repository;

import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID>, JpaSpecificationExecutor<Opportunity> {

    Optional<Opportunity> findByIdAndUserId(UUID id, UUID userId);

    List<Opportunity> findAllByUserId(UUID userId);

    long countByUserIdAndStatus(UUID userId, OpportunityStatus status);

    long countByUserIdAndStatusNot(UUID userId, OpportunityStatus status);

    long countByUserIdAndOpportunityTypeAndStatusNot(UUID userId, OpportunityType opportunityType, OpportunityStatus status);

    List<Opportunity> findTop5ByUserIdAndStatusNotOrderByCreatedAtDesc(UUID userId, OpportunityStatus status);

    List<Opportunity> findByUserIdAndStatusNotAndDeadlineAtIsNotNullAndDeadlineAtGreaterThanEqualOrderByDeadlineAtAsc(
            UUID userId, OpportunityStatus status, Instant now, Pageable pageable);

    @Query("SELECT o FROM Opportunity o WHERE o.userId = :userId AND o.status != :excludedStatus AND o.deadlineAt IS NOT NULL AND o.deadlineAt BETWEEN :start AND :end ORDER BY o.deadlineAt ASC")
    List<Opportunity> findUpcomingBetween(
            @Param("userId") UUID userId,
            @Param("excludedStatus") OpportunityStatus excludedStatus,
            @Param("start") Instant start,
            @Param("end") Instant end,
            Pageable pageable);

    long countByUserIdAndStatusIn(UUID userId, Collection<OpportunityStatus> statuses);

    long countByUserIdAndStatusNotInAndDeadlineAtIsNotNullAndDeadlineAtGreaterThanEqual(
            UUID userId, Collection<OpportunityStatus> excludedStatuses, Instant now);

    @Query("SELECT o.opportunityType, COUNT(o) FROM Opportunity o WHERE o.userId = :userId AND o.status != :excludedStatus GROUP BY o.opportunityType")
    List<Object[]> countGroupedByCategory(@Param("userId") UUID userId, @Param("excludedStatus") OpportunityStatus excludedStatus);

    @Query("SELECT o.status, COUNT(o) FROM Opportunity o WHERE o.userId = :userId GROUP BY o.status")
    List<Object[]> countGroupedByStatus(@Param("userId") UUID userId);

    @Query("SELECT o.priority, COUNT(o) FROM Opportunity o WHERE o.userId = :userId AND o.status != :excludedStatus GROUP BY o.priority")
    List<Object[]> countGroupedByPriority(@Param("userId") UUID userId, @Param("excludedStatus") OpportunityStatus excludedStatus);

    @Query("SELECT o FROM Opportunity o WHERE o.userId = :userId AND o.status != :excludedStatus AND (LOWER(o.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(o.organization) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Opportunity> searchByKeyword(
            @Param("userId") UUID userId,
            @Param("excludedStatus") OpportunityStatus excludedStatus,
            @Param("keyword") String keyword,
            Pageable pageable);

    List<Opportunity> findByUserIdAndStatusNotAndPriorityInOrderByDeadlineAtAsc(
            UUID userId, OpportunityStatus excludedStatus, Collection<String> priorities, Pageable pageable);

    List<Opportunity> findTop20ByUserIdAndStatusNotOrderByCreatedAtDesc(UUID userId, OpportunityStatus excludedStatus);

}
