package com.mnesa.backend.modules.intake.service;

import com.mnesa.backend.common.exception.MnesaException;
import com.mnesa.backend.modules.intake.domain.*;
import com.mnesa.backend.modules.intake.dto.IntakeRequest;
import com.mnesa.backend.modules.intake.dto.IntakeResponse;
import com.mnesa.backend.modules.intake.repository.CaptureRepository;
import com.mnesa.backend.modules.intake.repository.IntakeJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class IntakeService {

    private static final Logger log = LoggerFactory.getLogger(IntakeService.class);
    private static final Duration DUPLICATE_WINDOW = Duration.ofDays(7);

    private final CaptureRepository captureRepository;
    private final IntakeJobRepository intakeJobRepository;
    private final UrlSanitizerService urlSanitizerService;
    private final com.mnesa.backend.modules.ai.repository.AiExtractionRepository aiExtractionRepository;
    private final com.mnesa.backend.modules.opportunity.repository.OpportunityRepository opportunityRepository;
    private final com.mnesa.backend.modules.opportunity.repository.OpportunityActivityRepository activityRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final com.mnesa.backend.modules.ai.repository.AiCandidateRepository aiCandidateRepository;
    private final com.mnesa.backend.modules.attachment.repository.AttachmentRepository attachmentRepository;

    public IntakeService(CaptureRepository captureRepository,
                         IntakeJobRepository intakeJobRepository,
                         UrlSanitizerService urlSanitizerService,
                         com.mnesa.backend.modules.ai.repository.AiExtractionRepository aiExtractionRepository,
                         com.mnesa.backend.modules.opportunity.repository.OpportunityRepository opportunityRepository,
                         com.mnesa.backend.modules.opportunity.repository.OpportunityActivityRepository activityRepository,
                         com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this(captureRepository, intakeJobRepository, urlSanitizerService, aiExtractionRepository,
                opportunityRepository, activityRepository, objectMapper, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public IntakeService(CaptureRepository captureRepository,
                         IntakeJobRepository intakeJobRepository,

                         UrlSanitizerService urlSanitizerService,
                         com.mnesa.backend.modules.ai.repository.AiExtractionRepository aiExtractionRepository,
                         com.mnesa.backend.modules.opportunity.repository.OpportunityRepository opportunityRepository,
                         com.mnesa.backend.modules.opportunity.repository.OpportunityActivityRepository activityRepository,
                         com.fasterxml.jackson.databind.ObjectMapper objectMapper,
                         com.mnesa.backend.modules.ai.repository.AiCandidateRepository aiCandidateRepository,
                         com.mnesa.backend.modules.attachment.repository.AttachmentRepository attachmentRepository) {
        this.captureRepository = captureRepository;
        this.intakeJobRepository = intakeJobRepository;
        this.urlSanitizerService = urlSanitizerService;
        this.aiExtractionRepository = aiExtractionRepository;
        this.opportunityRepository = opportunityRepository;
        this.activityRepository = activityRepository;
        this.objectMapper = objectMapper;
        this.aiCandidateRepository = aiCandidateRepository;
        this.attachmentRepository = attachmentRepository;
    }


    /**
     * Ingests a new share capture with strict idempotency and SSRF validation.
     */
    @Transactional
    public IntakeResponse processIntake(UUID userId, IntakeRequest request) {
        if (userId == null) {
            throw new MnesaException("User ID is required for intake processing", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_INTAKE");
        }

        // Validate payload content existence
        boolean hasText = request.getText() != null && !request.getText().isBlank();
        boolean hasUrl = request.getUrl() != null && !request.getUrl().isBlank();
        boolean hasMedia = request.getMediaMimeType() != null && !request.getMediaMimeType().isBlank();

        if (!hasText && !hasUrl && !hasMedia) {
            throw new MnesaException("Intake payload must contain text, a URL, or media content",
                    HttpStatus.BAD_REQUEST, "EMPTY_INTAKE_PAYLOAD");
        }

        // 1. Enforce Idempotency check
        Optional<Capture> existingCaptureOpt = captureRepository.findByUserIdAndIdempotencyKey(userId, request.getIdempotencyKey());
        if (existingCaptureOpt.isPresent()) {
            Capture existing = existingCaptureOpt.get();
            Optional<IntakeJob> existingJob = intakeJobRepository.findByCaptureId(existing.getId());
            log.info("Idempotent intake hit for user [{}] and key [{}]", userId, request.getIdempotencyKey());
            return IntakeResponse.builder()
                    .captureId(existing.getId())
                    .jobId(existingJob.map(IntakeJob::getId).orElse(null))
                    .status(existing.getStatus().name())
                    .sourceType(existing.getSourceType().name())
                    .canonicalUrl(existing.getCanonicalUrl())
                    .message("Opportunity already captured (idempotent request)")
                    .duplicate(true)
                    .createdAt(existing.getCreatedAt())
                    .build();
        }

        // 2. Extract and Sanitize URL
        String rawUrl = request.getUrl();
        if ((rawUrl == null || rawUrl.isBlank()) && hasText) {
            rawUrl = urlSanitizerService.extractFirstUrl(request.getText());
        }

        String canonicalUrl = null;
        String sourceDomain = null;
        if (rawUrl != null && !rawUrl.isBlank()) {
            canonicalUrl = urlSanitizerService.sanitizeAndNormalize(rawUrl);
            sourceDomain = urlSanitizerService.extractDomain(canonicalUrl);
        }

        // 3. Classify Source Type
        SourceType sourceType = classifySource(request, canonicalUrl, hasMedia, hasText);

        // 4. Duplicate Check (within recent duplicate window)
        boolean isDuplicate = false;
        String userMessage = "Opportunity captured and queued for intelligent analysis";
        if (canonicalUrl != null) {
            Optional<Capture> previousCapture = captureRepository.findFirstByUserIdAndCanonicalUrlOrderByCreatedAtDesc(userId, canonicalUrl);
            if (previousCapture.isPresent()) {
                Instant prevCreatedAt = previousCapture.get().getCreatedAt();
                if (prevCreatedAt != null && Duration.between(prevCreatedAt, Instant.now()).compareTo(DUPLICATE_WINDOW) < 0) {
                    isDuplicate = true;
                    userMessage = "Opportunity with this URL was previously captured; queued as an update";
                }
            }
        }

        // 5. Persist Capture
        String captureMetadata = (request.getImageBase64() != null && !request.getImageBase64().isBlank())
                ? request.getImageBase64()
                : request.getMetadata();

        Capture capture = Capture.builder()
                .userId(userId)
                .sourceType(sourceType)
                .originalText(request.getText())
                .originalUrl(rawUrl)
                .canonicalUrl(canonicalUrl)
                .sourceDomain(sourceDomain)
                .clientCaptureId(request.getClientCaptureId())
                .idempotencyKey(request.getIdempotencyKey())
                .mediaMimeType(request.getMediaMimeType())
                .mediaSizeBytes(request.getMediaSizeBytes())
                .metadata(captureMetadata)
                .status(CaptureStatus.RECEIVED)
                .build();
        Capture savedCapture = captureRepository.save(capture);

        if (attachmentRepository != null && request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
            attachmentRepository.save(com.mnesa.backend.modules.attachment.domain.Attachment.builder()
                    .userId(userId)
                    .captureId(savedCapture.getId())
                    .fileName("screenshot_" + System.currentTimeMillis() + ".png")
                    .storagePath("inline_base64")
                    .mimeType(request.getMediaMimeType() != null ? request.getMediaMimeType() : "image/png")
                    .fileSizeBytes(request.getMediaSizeBytes() != null ? request.getMediaSizeBytes() : (long) request.getImageBase64().length())
                    .build());
        }

        // 6. Persist Asynchronous Intake Job
        IntakeJob job = IntakeJob.builder()
                .captureId(savedCapture.getId())
                .userId(userId)
                .status(IntakeJobStatus.PENDING)
                .attemptCount(0)
                .build();
        IntakeJob savedJob = intakeJobRepository.save(job);

        log.info("Successfully ingested intake capture [{}] with job [{}] for user [{}] (source: {})",
                savedCapture.getId(), savedJob.getId(), userId, sourceType);

        return IntakeResponse.builder()
                .captureId(savedCapture.getId())
                .jobId(savedJob.getId())
                .status(savedCapture.getStatus().name())
                .sourceType(sourceType.name())
                .canonicalUrl(canonicalUrl)
                .message(userMessage)
                .duplicate(isDuplicate)
                .createdAt(savedCapture.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Capture getCapture(UUID userId, UUID captureId) {
        return captureRepository.findById(captureId)
                .filter(c -> c.getUserId().equals(userId))
                .orElseThrow(() -> new MnesaException("Capture not found", HttpStatus.NOT_FOUND, "CAPTURE_NOT_FOUND"));
    }

    @Transactional(readOnly = true)
    public IntakeJob getIntakeJob(UUID userId, UUID jobId) {
        return intakeJobRepository.findById(jobId)
                .filter(j -> j.getUserId().equals(userId))
                .orElseThrow(() -> new MnesaException("Intake job not found", HttpStatus.NOT_FOUND, "JOB_NOT_FOUND"));
    }

    @Transactional(readOnly = true)
    public com.mnesa.backend.modules.intake.dto.IntakeJobStatusResponse getIntakeJobStatus(UUID userId, UUID jobId) {
        IntakeJob job = getIntakeJob(userId, jobId);

        com.mnesa.backend.modules.intake.dto.AiExtractionDto extractionDto = null;
        java.util.List<com.mnesa.backend.modules.intake.dto.AiCandidateDto> candidateDtos = new java.util.ArrayList<>();

        if (job.getStatus() == IntakeJobStatus.COMPLETED) {
            java.util.Optional<com.mnesa.backend.modules.ai.domain.AiExtraction> extractionOpt = aiExtractionRepository.findByIntakeJobId(jobId);
            if (extractionOpt.isPresent()) {
                com.mnesa.backend.modules.ai.domain.AiExtraction ex = extractionOpt.get();
                java.util.List<String> evidenceList = new java.util.ArrayList<>();
                java.util.List<String> warningList = new java.util.ArrayList<>();
                if (ex.getEvidenceSnippets() != null) {
                    try {
                        evidenceList = objectMapper.readValue(ex.getEvidenceSnippets(), new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
                    } catch (Exception ignored) {}
                }
                if (ex.getWarningMessages() != null) {
                    try {
                        warningList = objectMapper.readValue(ex.getWarningMessages(), new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
                    } catch (Exception ignored) {}
                }

                extractionDto = com.mnesa.backend.modules.intake.dto.AiExtractionDto.builder()
                        .id(ex.getId())
                        .title(ex.getTitle())
                        .organization(ex.getOrganization())
                        .category(ex.getCategory())
                        .summary(ex.getSummary())
                        .deadlineAt(ex.getDeadlineAt())
                        .deadlineRaw(ex.getDeadlineRaw())
                        .deadlineAmbiguous(ex.isDeadlineAmbiguous())
                        .registrationUrl(ex.getRegistrationUrl())
                        .location(ex.getLocation())
                        .workMode(ex.getWorkMode() != null ? ex.getWorkMode().name() : null)
                        .eligibility(ex.getEligibility())
                        .estimatedEffortMinutes(ex.getEstimatedEffortMinutes())
                        .priority(ex.getPriority() != null ? ex.getPriority().name() : "NORMAL")
                        .priorityReason(ex.getPriorityReason())
                        .overallConfidence(ex.getOverallConfidence())
                        .validationStatus(ex.getValidationStatus() != null ? ex.getValidationStatus().name() : "SUCCEEDED")
                        .evidenceSnippets(evidenceList)
                        .warningMessages(warningList)
                        .processingDurationMs(ex.getProcessingDurationMs())
                        .build();

                if (aiCandidateRepository != null) {
                    java.util.List<com.mnesa.backend.modules.ai.domain.AiCandidate> candidates =
                            aiCandidateRepository.findAllByExtractionIdOrderByCandidateIndexAsc(ex.getId());
                    for (com.mnesa.backend.modules.ai.domain.AiCandidate c : candidates) {
                        candidateDtos.add(com.mnesa.backend.modules.intake.dto.AiCandidateDto.builder()
                                .id(c.getId())
                                .candidateIndex(c.getCandidateIndex())
                                .title(c.getTitle())
                                .organization(c.getOrganization())
                                .category(c.getCategory())
                                .summary(c.getSummary())
                                .deadlineAt(c.getDeadlineAt())
                                .confidenceScore(c.getConfidenceScore())
                                .isConfirmed(c.isConfirmed())
                                .build());
                    }
                }
            }
        }

        return com.mnesa.backend.modules.intake.dto.IntakeJobStatusResponse.builder()
                .jobId(job.getId())
                .captureId(job.getCaptureId())
                .status(job.getStatus().name())
                .attemptCount(job.getAttemptCount())
                .errorMessage(job.getErrorMessage())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .extraction(extractionDto)
                .candidates(candidateDtos)
                .build();
    }

    @Transactional
    public com.mnesa.backend.modules.opportunity.domain.Opportunity confirmIntakeJob(UUID userId, UUID jobId, com.mnesa.backend.modules.intake.dto.ConfirmOpportunityRequest request) {
        IntakeJob job = getIntakeJob(userId, jobId);
        Capture capture = getCapture(userId, job.getCaptureId());

        java.util.Optional<com.mnesa.backend.modules.ai.domain.AiExtraction> extractionOpt = aiExtractionRepository.findByIntakeJobId(jobId);

        String title = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getTitle).orElse("Captured Opportunity");
        if (request != null && request.getTitle() != null && !request.getTitle().isBlank()) {
            title = request.getTitle().trim();
        }

        String organization = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getOrganization).orElse(null);
        if (request != null && request.getOrganization() != null && !request.getOrganization().isBlank()) {
            organization = request.getOrganization().trim();
        }

        String category = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getCategory).orElse("OTHER");
        if (request != null && request.getCategory() != null && !request.getCategory().isBlank()) {
            category = request.getCategory().trim().toUpperCase();
        }

        java.time.Instant deadlineAt = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getDeadlineAt).orElse(null);
        if (request != null && request.getDeadlineAt() != null) {
            deadlineAt = request.getDeadlineAt();
        }

        if (request != null && request.getCandidateId() != null && aiCandidateRepository != null) {
            java.util.Optional<com.mnesa.backend.modules.ai.domain.AiCandidate> candOpt = aiCandidateRepository.findById(request.getCandidateId());
            if (candOpt.isPresent()) {
                com.mnesa.backend.modules.ai.domain.AiCandidate cand = candOpt.get();
                if (request.getTitle() == null || request.getTitle().isBlank()) {
                    title = cand.getTitle();
                }
                if (request.getOrganization() == null || request.getOrganization().isBlank()) {
                    organization = cand.getOrganization();
                }
                if (request.getCategory() == null || request.getCategory().isBlank()) {
                    category = cand.getCategory();
                }
                if (request.getDeadlineAt() == null) {
                    deadlineAt = cand.getDeadlineAt();
                }
                cand.setConfirmed(true);
                aiCandidateRepository.save(cand);
            }
        }

        com.mnesa.backend.modules.opportunity.domain.OpportunityType oppType = com.mnesa.backend.modules.opportunity.domain.OpportunityType.OTHER;
        try {
            oppType = com.mnesa.backend.modules.opportunity.domain.OpportunityType.valueOf(category);
        } catch (IllegalArgumentException ignored) {}


        java.math.BigDecimal confidence = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getOverallConfidence).orElse(java.math.BigDecimal.valueOf(0.8));
        String description = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getSummary).orElse(null);
        String registrationUrl = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getRegistrationUrl).orElse(null);
        String location = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getLocation).orElse(null);
        String eligibility = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getEligibility).orElse(null);
        String workMode = extractionOpt.map(ex -> ex.getWorkMode() != null ? ex.getWorkMode().name() : "UNSPECIFIED").orElse("UNSPECIFIED");
        String estimatedEffort = extractionOpt.map(ex -> ex.getEstimatedEffortMinutes() != null ? ex.getEstimatedEffortMinutes() + " mins" : null).orElse(null);
        String priority = extractionOpt.map(ex -> ex.getPriority() != null ? ex.getPriority().name() : "NORMAL").orElse("NORMAL");
        String priorityReason = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getPriorityReason).orElse(null);
        UUID extractionId = extractionOpt.map(com.mnesa.backend.modules.ai.domain.AiExtraction::getId).orElse(null);

        com.mnesa.backend.modules.opportunity.domain.Opportunity opportunity = com.mnesa.backend.modules.opportunity.domain.Opportunity.builder()
                .userId(userId)
                .title(title)
                .organization(organization)
                .opportunityType(oppType)
                .description(description)
                .sourceUrl(capture.getCanonicalUrl() != null ? capture.getCanonicalUrl() : capture.getOriginalUrl())
                .registrationUrl(registrationUrl)
                .sourceDomain(capture.getSourceDomain())
                .rawContent(capture.getOriginalText())
                .status(com.mnesa.backend.modules.opportunity.domain.OpportunityStatus.SAVED)
                .deadlineAt(deadlineAt)
                .location(location)
                .workMode(workMode)
                .eligibility(eligibility)
                .estimatedEffort(estimatedEffort)
                .priority(priority)
                .priorityReason(priorityReason)
                .confidenceScore(confidence)
                .extractionId(extractionId)
                .lastStatusChangeAt(java.time.Instant.now())
                .build();

        com.mnesa.backend.modules.opportunity.domain.Opportunity saved = opportunityRepository.save(opportunity);

        if (activityRepository != null) {
            activityRepository.save(com.mnesa.backend.modules.opportunity.domain.OpportunityActivity.builder()
                    .opportunityId(saved.getId())
                    .userId(userId)
                    .actionType("CONFIRMED")
                    .newStatus(com.mnesa.backend.modules.opportunity.domain.OpportunityStatus.SAVED.name())
                    .description("Confirmed from AI extraction proposal")
                    .build());
        }

        return saved;
    }

    private SourceType classifySource(IntakeRequest request, String canonicalUrl, boolean hasMedia, boolean hasText) {
        if (request.getSourceType() != null && !request.getSourceType().isBlank()) {
            try {
                return SourceType.valueOf(request.getSourceType().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // fallback to auto-detection
            }
        }

        if (hasMedia) {
            return (canonicalUrl != null || (hasText && request.getText().length() > 50))
                    ? SourceType.HYBRID
                    : SourceType.IMAGE;
        }

        if (canonicalUrl != null) {
            if (!hasText || request.getText().trim().equalsIgnoreCase(canonicalUrl) || request.getText().length() < 60) {
                return SourceType.URL;
            }
            return SourceType.HYBRID;
        }

        return SourceType.TEXT;
    }
}
