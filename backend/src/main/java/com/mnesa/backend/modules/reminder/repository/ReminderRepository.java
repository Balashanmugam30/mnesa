package com.mnesa.backend.modules.reminder.repository;

import com.mnesa.backend.modules.reminder.domain.Reminder;
import com.mnesa.backend.modules.reminder.domain.ReminderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {

    Optional<Reminder> findByIdAndUserId(UUID id, UUID userId);

    List<Reminder> findByOpportunityIdAndUserId(UUID opportunityId, UUID userId);

    List<Reminder> findByOpportunityId(UUID opportunityId);

    Page<Reminder> findByUserIdOrderByScheduledAtAsc(UUID userId, Pageable pageable);

    Page<Reminder> findByUserIdAndStatusOrderByScheduledAtAsc(UUID userId, ReminderStatus status, Pageable pageable);

    Page<Reminder> findByUserIdAndStatusInOrderByScheduledAtAsc(UUID userId, Collection<ReminderStatus> statuses, Pageable pageable);

    long countByUserIdAndStatus(UUID userId, ReminderStatus status);

    long countByUserIdAndStatusIn(UUID userId, Collection<ReminderStatus> statuses);

    @Query("SELECT r FROM Reminder r WHERE r.status IN ('SCHEDULED', 'SNOOZED') AND " +
           "((r.snoozeUntil IS NOT NULL AND r.snoozeUntil <= :now) OR " +
           "(r.snoozeUntil IS NULL AND r.scheduledAt <= :now)) ORDER BY r.scheduledAt ASC")
    List<Reminder> findDueReminders(@Param("now") Instant now, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Reminder r WHERE r.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}
