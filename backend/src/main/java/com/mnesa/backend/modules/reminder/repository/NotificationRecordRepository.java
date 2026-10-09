package com.mnesa.backend.modules.reminder.repository;

import com.mnesa.backend.modules.reminder.domain.DeliveryStatus;
import com.mnesa.backend.modules.reminder.domain.NotificationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, UUID> {

    Page<NotificationRecord> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<NotificationRecord> findByIdAndUserId(UUID id, UUID userId);

    long countByUserIdAndDeliveryStatus(UUID userId, DeliveryStatus status);

    long countByUserIdAndOpenedAtIsNull(UUID userId);
}
