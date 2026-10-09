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

    public IntakeService(CaptureRepository captureRepository,
                         IntakeJobRepository intakeJobRepository,
                         UrlSanitizerService urlSanitizerService) {
        this.captureRepository = captureRepository;
        this.intakeJobRepository = intakeJobRepository;
        this.urlSanitizerService = urlSanitizerService;
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
                .metadata(request.getMetadata())
                .status(CaptureStatus.RECEIVED)
                .build();
        Capture savedCapture = captureRepository.save(capture);

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
