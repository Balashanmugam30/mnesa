package com.mnesa.backend.modules.opportunity.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.opportunity.domain.*;
import com.mnesa.backend.modules.opportunity.dto.*;
import com.mnesa.backend.modules.opportunity.repository.OpportunityActivityRepository;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import com.mnesa.backend.modules.opportunity.repository.OpportunitySpecification;
import com.mnesa.backend.modules.opportunity.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.mnesa.backend.modules.reminder.domain.Reminder;
import com.mnesa.backend.modules.reminder.domain.ReminderStatus;
import com.mnesa.backend.modules.reminder.repository.ReminderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityActivityRepository activityRepository;
    private final TagRepository tagRepository;
    private final OpportunityTransitionService transitionService;
    private final ReminderRepository reminderRepository;

    @Transactional
    public OpportunityResponse createOpportunity(UUID userId, CreateOpportunityRequest request) {
        OpportunityType oppType = OpportunityType.OTHER;
        if (request.getCategory() != null) {
            try {
                oppType = OpportunityType.valueOf(request.getCategory().trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        Opportunity opportunity = Opportunity.builder()
                .userId(userId)
                .title(request.getTitle().trim())
                .organization(request.getOrganization() != null ? request.getOrganization().trim() : null)
                .opportunityType(oppType)
                .description(request.getDescription())
                .sourceUrl(request.getSourceUrl())
                .registrationUrl(request.getRegistrationUrl())
                .deadlineAt(request.getDeadlineAt())
                .deadlineTimezone(request.getDeadlineTimezone())
                .eligibility(request.getEligibility())
                .location(request.getLocation())
                .workMode(request.getWorkMode() != null ? request.getWorkMode() : "UNSPECIFIED")
                .estimatedEffort(request.getEstimatedEffort())
                .priority(request.getPriority() != null ? request.getPriority().toUpperCase() : "NORMAL")
                .notes(request.getNotes())
                .status(OpportunityStatus.SAVED)
                .lastStatusChangeAt(Instant.now())
                .build();

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = resolveOrCreateTags(userId, request.getTags());
            opportunity.setTags(tags);
        }

        Opportunity saved = opportunityRepository.save(opportunity);

        recordActivity(saved.getId(), userId, "CREATED", null, OpportunityStatus.SAVED.name(),
                "Manually created opportunity", null);

        log.info("Opportunity created: id={} userId={}", saved.getId(), userId);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OpportunityResponse getOpportunity(UUID userId, UUID id) {
        Opportunity opportunity = opportunityRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));
        return toResponse(opportunity);
    }

    @Transactional
    public OpportunityResponse updateOpportunity(UUID userId, UUID id, UpdateOpportunityRequest request) {
        Opportunity opportunity = opportunityRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            opportunity.setTitle(request.getTitle().trim());
        }
        if (request.getOrganization() != null) {
            opportunity.setOrganization(request.getOrganization().trim());
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            try {
                opportunity.setOpportunityType(OpportunityType.valueOf(request.getCategory().trim().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (request.getDescription() != null) {
            opportunity.setDescription(request.getDescription());
        }
        if (request.getSourceUrl() != null) {
            opportunity.setSourceUrl(request.getSourceUrl());
        }
        if (request.getRegistrationUrl() != null) {
            opportunity.setRegistrationUrl(request.getRegistrationUrl());
        }
        if (request.getDeadlineAt() != null) {
            opportunity.setDeadlineAt(request.getDeadlineAt());
        }
        if (request.getDeadlineTimezone() != null) {
            opportunity.setDeadlineTimezone(request.getDeadlineTimezone());
        }
        if (request.getEligibility() != null) {
            opportunity.setEligibility(request.getEligibility());
        }
        if (request.getLocation() != null) {
            opportunity.setLocation(request.getLocation());
        }
        if (request.getWorkMode() != null) {
            opportunity.setWorkMode(request.getWorkMode());
        }
        if (request.getEstimatedEffort() != null) {
            opportunity.setEstimatedEffort(request.getEstimatedEffort());
        }
        if (request.getPriority() != null) {
            opportunity.setPriority(request.getPriority().toUpperCase());
        }
        if (request.getNotes() != null) {
            opportunity.setNotes(request.getNotes());
        }
        if (request.getTags() != null) {
            Set<Tag> tags = resolveOrCreateTags(userId, request.getTags());
            opportunity.setTags(tags);
        }

        Opportunity updated = opportunityRepository.save(opportunity);
        recordActivity(updated.getId(), userId, "EDITED", null, updated.getStatus().name(),
                "Updated opportunity details", null);

        log.info("Opportunity updated: id={} userId={}", updated.getId(), userId);
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    public Page<OpportunitySummaryResponse> searchOpportunities(
            UUID userId,
            String query,
            String category,
            String status,
            String priority,
            Instant deadlineBefore,
            Instant deadlineAfter,
            String organization,
            String location,
            String tag,
            boolean includeArchived,
            Pageable pageable) {

        // Enforce max page size of 50
        int pageSize = Math.min(pageable.getPageSize(), 50);
        Pageable boundedPageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        Page<Opportunity> page = opportunityRepository.findAll(
                OpportunitySpecification.filter(userId, query, category, status, priority,
                        deadlineBefore, deadlineAfter, organization, location, tag, includeArchived),
                boundedPageable
        );

        return page.map(this::toSummaryResponse);
    }

    @Transactional
    public OpportunityResponse updateStatus(UUID userId, UUID id, UpdateStatusRequest request) {
        Opportunity opportunity = opportunityRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));

        OpportunityStatus targetStatus = OpportunityStatus.valueOf(request.getStatus().trim().toUpperCase());
        OpportunityStatus currentStatus = opportunity.getStatus();

        transitionService.validateTransition(currentStatus, targetStatus);

        opportunity.setPreviousStatus(currentStatus.name());
        opportunity.setStatus(targetStatus);
        opportunity.setLastStatusChangeAt(Instant.now());

        if (targetStatus == OpportunityStatus.ARCHIVED) {
            opportunity.setArchivedAt(Instant.now());
        } else if (currentStatus == OpportunityStatus.ARCHIVED) {
            opportunity.setArchivedAt(null);
        }

        Opportunity updated = opportunityRepository.save(opportunity);

        if (targetStatus == OpportunityStatus.APPLIED ||
            targetStatus == OpportunityStatus.ARCHIVED ||
            targetStatus == OpportunityStatus.SELECTED ||
            targetStatus == OpportunityStatus.REJECTED ||
            targetStatus == OpportunityStatus.MISSED) {
            cancelPendingReminders(updated.getId());
        }

        String description = request.getNote() != null && !request.getNote().isBlank()
                ? request.getNote().trim()
                : "Lifecycle status transitioned from " + currentStatus + " to " + targetStatus;

        recordActivity(updated.getId(), userId, "STATUS_CHANGED", currentStatus.name(), targetStatus.name(),
                description, null);

        log.info("Opportunity status updated: id={} userId={} from={} to={}", id, userId, currentStatus, targetStatus);
        return toResponse(updated);
    }

    @Transactional
    public OpportunityResponse archiveOpportunity(UUID userId, UUID id) {
        Opportunity opportunity = opportunityRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));

        OpportunityStatus currentStatus = opportunity.getStatus();
        if (currentStatus == OpportunityStatus.ARCHIVED) {
            return toResponse(opportunity);
        }

        transitionService.validateTransition(currentStatus, OpportunityStatus.ARCHIVED);

        opportunity.setPreviousStatus(currentStatus.name());
        opportunity.setStatus(OpportunityStatus.ARCHIVED);
        opportunity.setArchivedAt(Instant.now());
        opportunity.setLastStatusChangeAt(Instant.now());

        Opportunity saved = opportunityRepository.save(opportunity);
        cancelPendingReminders(saved.getId());

        recordActivity(saved.getId(), userId, "ARCHIVED", currentStatus.name(), OpportunityStatus.ARCHIVED.name(),
                "Opportunity moved to archive", null);

        log.info("Opportunity archived: id={} userId={}", id, userId);
        return toResponse(saved);
    }

    private void cancelPendingReminders(UUID opportunityId) {
        try {
            List<Reminder> reminders = reminderRepository.findByOpportunityId(opportunityId);
            for (Reminder r : reminders) {
                if (r.getStatus() == ReminderStatus.SCHEDULED || r.getStatus() == ReminderStatus.SNOOZED) {
                    r.cancel();
                    reminderRepository.save(r);
                    log.info("Auto-cancelled pending reminder {} for opportunity {}", r.getId(), opportunityId);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to auto-cancel reminders for opportunity {}: {}", opportunityId, e.getMessage());
        }
    }

    @Transactional
    public OpportunityResponse restoreOpportunity(UUID userId, UUID id) {
        Opportunity opportunity = opportunityRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));

        if (opportunity.getStatus() != OpportunityStatus.ARCHIVED) {
            return toResponse(opportunity);
        }

        OpportunityStatus restoreTo = OpportunityStatus.SAVED;
        if (opportunity.getPreviousStatus() != null) {
            try {
                restoreTo = OpportunityStatus.valueOf(opportunity.getPreviousStatus());
            } catch (IllegalArgumentException ignored) {}
        }

        opportunity.setStatus(restoreTo);
        opportunity.setArchivedAt(null);
        opportunity.setLastStatusChangeAt(Instant.now());

        Opportunity saved = opportunityRepository.save(opportunity);
        recordActivity(saved.getId(), userId, "RESTORED", OpportunityStatus.ARCHIVED.name(), restoreTo.name(),
                "Opportunity restored to " + restoreTo.name(), null);

        log.info("Opportunity restored: id={} userId={} to={}", id, userId, restoreTo);
        return toResponse(saved);
    }

    @Transactional
    public void deleteOpportunity(UUID userId, UUID id) {
        Opportunity opportunity = opportunityRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));
        opportunityRepository.delete(opportunity);
        log.info("Opportunity permanently deleted: id={} userId={}", id, userId);
    }

    @Transactional(readOnly = true)
    public List<OpportunityActivityResponse> getOpportunityHistory(UUID userId, UUID id) {
        // Verify ownership
        opportunityRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));

        return activityRepository.findAllByOpportunityIdAndUserIdOrderByCreatedAtDesc(id, userId).stream()
                .map(this::toActivityResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HomeDashboardResponse getHomeDashboard(UUID userId) {
        Instant now = Instant.now();
        Instant threeDaysOut = now.plus(Duration.ofDays(3));

        // Time-aware greeting
        ZonedDateTime userZdt = ZonedDateTime.ofInstant(now, ZoneId.systemDefault());
        int hour = userZdt.getHour();
        String greeting = (hour < 12) ? "Good morning." : (hour < 17) ? "Good afternoon." : "Good evening.";

        // Status counts
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (OpportunityStatus s : OpportunityStatus.values()) {
            if (s != OpportunityStatus.CAPTURED && s != OpportunityStatus.PROCESSING && s != OpportunityStatus.UNDERSTOOD) {
                long c = opportunityRepository.countByUserIdAndStatus(userId, s);
                statusCounts.put(s.name(), c);
            }
        }
        long totalActive = opportunityRepository.countByUserIdAndStatusNot(userId, OpportunityStatus.ARCHIVED);

        // Needs attention: upcoming deadlines in <= 3 days, or URGENT priority, excluding ARCHIVED
        List<Opportunity> needsAttentionList = opportunityRepository.findUpcomingBetween(
                userId, OpportunityStatus.ARCHIVED, now, threeDaysOut, PageRequest.of(0, 5));

        // Upcoming opportunities: deadlines >= now, ordered by deadline ascending
        List<Opportunity> upcomingList = opportunityRepository
                .findByUserIdAndStatusNotAndDeadlineAtIsNotNullAndDeadlineAtGreaterThanEqualOrderByDeadlineAtAsc(
                        userId, OpportunityStatus.ARCHIVED, now, PageRequest.of(0, 5));

        // Recently saved: created desc, limit 5
        List<Opportunity> recentlySavedList = opportunityRepository
                .findTop5ByUserIdAndStatusNotOrderByCreatedAtDesc(userId, OpportunityStatus.ARCHIVED);

        // Suggested next action (deterministic and explainable)
        SuggestedActionResponse suggestedAction = determineSuggestedAction(needsAttentionList, upcomingList, recentlySavedList);

        return HomeDashboardResponse.builder()
                .greeting(greeting)
                .statusCounts(statusCounts)
                .totalActive(totalActive)
                .needsAttention(needsAttentionList.stream().map(this::toSummaryResponse).collect(Collectors.toList()))
                .upcoming(upcomingList.stream().map(this::toSummaryResponse).collect(Collectors.toList()))
                .recentlySaved(recentlySavedList.stream().map(this::toSummaryResponse).collect(Collectors.toList()))
                .suggestedAction(suggestedAction)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getCategories(UUID userId) {
        List<CategorySummaryResponse> list = new ArrayList<>();
        for (OpportunityType type : OpportunityType.values()) {
            long count = opportunityRepository.countByUserIdAndOpportunityTypeAndStatusNot(
                    userId, type, OpportunityStatus.ARCHIVED);
            list.add(CategorySummaryResponse.builder()
                    .category(type.name())
                    .displayName(formatDisplayName(type.name()))
                    .activeCount(count)
                    .build());
        }
        return list;
    }

    private SuggestedActionResponse determineSuggestedAction(
            List<Opportunity> needsAttention,
            List<Opportunity> upcoming,
            List<Opportunity> recentlySaved) {

        if (needsAttention != null && !needsAttention.isEmpty()) {
            Opportunity urgent = needsAttention.get(0);
            return SuggestedActionResponse.builder()
                    .title("Approaching Deadline: " + urgent.getTitle())
                    .description("Deadline is within 3 days. Review requirements and submit application.")
                    .opportunityId(urgent.getId())
                    .actionType("SUBMIT_APPLICATION")
                    .build();
        }

        if (recentlySaved != null && !recentlySaved.isEmpty()) {
            Opportunity recent = recentlySaved.get(0);
            if (recent.getStatus() == OpportunityStatus.SAVED) {
                return SuggestedActionResponse.builder()
                        .title("Review Saved Opportunity: " + recent.getTitle())
                        .description("You recently saved this opportunity. Review key facts and decide next steps.")
                        .opportunityId(recent.getId())
                        .actionType("REVIEW_OPPORTUNITY")
                        .build();
            }
        }

        if (upcoming != null && !upcoming.isEmpty()) {
            Opportunity next = upcoming.get(0);
            return SuggestedActionResponse.builder()
                    .title("Upcoming: " + next.getTitle())
                    .description("Prepare your application materials for your upcoming deadline.")
                    .opportunityId(next.getId())
                    .actionType("PREPARE_APPLICATION")
                    .build();
        }

        return SuggestedActionResponse.builder()
                .title("Share opportunities to MNESA")
                .description("Share any internship, hackathon, or scholarship from other apps to begin.")
                .opportunityId(null)
                .actionType("CAPTURE_FIRST")
                .build();
    }

    private Set<Tag> resolveOrCreateTags(UUID userId, List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String rawName : tagNames) {
            if (rawName == null || rawName.isBlank()) continue;
            String normalized = rawName.trim().toLowerCase();
            Tag tag = tagRepository.findByUserIdAndName(userId, normalized)
                    .orElseGet(() -> tagRepository.save(Tag.builder().userId(userId).name(normalized).build()));
            tags.add(tag);
        }
        return tags;
    }

    private void recordActivity(UUID opportunityId, UUID userId, String actionType,
                                String oldStatus, String newStatus, String description, String metadata) {
        activityRepository.save(OpportunityActivity.builder()
                .opportunityId(opportunityId)
                .userId(userId)
                .actionType(actionType)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .description(description)
                .metadata(metadata)
                .build());
    }

    private OpportunityResponse toResponse(Opportunity o) {
        List<String> tagNames = o.getTags() != null
                ? o.getTags().stream().map(Tag::getName).sorted().collect(Collectors.toList())
                : Collections.emptyList();

        return OpportunityResponse.builder()
                .id(o.getId())
                .userId(o.getUserId())
                .title(o.getTitle())
                .organization(o.getOrganization())
                .category(o.getOpportunityType() != null ? o.getOpportunityType().name() : "OTHER")
                .description(o.getDescription())
                .sourceUrl(o.getSourceUrl())
                .registrationUrl(o.getRegistrationUrl())
                .sourceDomain(o.getSourceDomain())
                .status(o.getStatus() != null ? o.getStatus().name() : "SAVED")
                .previousStatus(o.getPreviousStatus())
                .deadlineAt(o.getDeadlineAt())
                .deadlineTimezone(o.getDeadlineTimezone())
                .eligibility(o.getEligibility())
                .location(o.getLocation())
                .workMode(o.getWorkMode())
                .estimatedEffort(o.getEstimatedEffort())
                .priority(o.getPriority())
                .priorityReason(o.getPriorityReason())
                .notes(o.getNotes())
                .confidenceScore(o.getConfidenceScore())
                .lastStatusChangeAt(o.getLastStatusChangeAt())
                .archivedAt(o.getArchivedAt())
                .extractionId(o.getExtractionId())
                .tags(tagNames)
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }

    private OpportunitySummaryResponse toSummaryResponse(Opportunity o) {
        List<String> tagNames = o.getTags() != null
                ? o.getTags().stream().map(Tag::getName).sorted().collect(Collectors.toList())
                : Collections.emptyList();

        String urgencyLabel = null;
        if (o.getDeadlineAt() != null) {
            long hours = Duration.between(Instant.now(), o.getDeadlineAt()).toHours();
            if (hours < 0) {
                urgencyLabel = "Expired";
            } else if (hours <= 24) {
                urgencyLabel = "Due in " + Math.max(1, hours) + "h";
            } else if (hours <= 72) {
                urgencyLabel = (hours / 24) + " days left";
            }
        }

        return OpportunitySummaryResponse.builder()
                .id(o.getId())
                .title(o.getTitle())
                .organization(o.getOrganization())
                .category(o.getOpportunityType() != null ? o.getOpportunityType().name() : "OTHER")
                .status(o.getStatus() != null ? o.getStatus().name() : "SAVED")
                .deadlineAt(o.getDeadlineAt())
                .priority(o.getPriority())
                .location(o.getLocation())
                .confidenceScore(o.getConfidenceScore())
                .tags(tagNames)
                .urgencyLabel(urgencyLabel)
                .createdAt(o.getCreatedAt())
                .build();
    }

    private OpportunityActivityResponse toActivityResponse(OpportunityActivity a) {
        return OpportunityActivityResponse.builder()
                .id(a.getId())
                .opportunityId(a.getOpportunityId())
                .actionType(a.getActionType())
                .oldStatus(a.getOldStatus())
                .newStatus(a.getNewStatus())
                .description(a.getDescription())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private String formatDisplayName(String enumName) {
        if (enumName == null) return "";
        return Arrays.stream(enumName.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
