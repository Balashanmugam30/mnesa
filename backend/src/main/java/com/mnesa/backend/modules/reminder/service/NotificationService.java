package com.mnesa.backend.modules.reminder.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.reminder.domain.NotificationRecord;
import com.mnesa.backend.modules.reminder.dto.NotificationRecordDto;
import com.mnesa.backend.modules.reminder.repository.NotificationRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRecordRepository notificationRecordRepository;

    public NotificationService(NotificationRecordRepository notificationRecordRepository) {
        this.notificationRecordRepository = notificationRecordRepository;
    }

    @Transactional(readOnly = true)
    public Page<NotificationRecordDto> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRecordRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toDto);
    }

    @Transactional
    public NotificationRecordDto markNotificationOpened(UUID userId, UUID notificationId) {
        NotificationRecord record = notificationRecordRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!record.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this notification");
        }

        record.markOpened();
        record = notificationRecordRepository.save(record);
        return toDto(record);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRecordRepository.countByUserIdAndOpenedAtIsNull(userId);
    }

    private NotificationRecordDto toDto(NotificationRecord record) {
        return NotificationRecordDto.builder()
                .id(record.getId())
                .userId(record.getUser().getId())
                .reminderId(record.getReminder() != null ? record.getReminder().getId() : null)
                .opportunityId(record.getOpportunity() != null ? record.getOpportunity().getId() : null)
                .title(record.getTitle())
                .body(record.getBody())
                .channel(record.getChannel().name())
                .provider(record.getProvider())
                .deliveryStatus(record.getDeliveryStatus().name())
                .deepLinkUri(record.getDeepLinkUri())
                .openedAt(record.getOpenedAt())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
