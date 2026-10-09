package com.mnesa.backend.modules.intake.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.ai.client.AiServiceClient;
import com.mnesa.backend.modules.ai.domain.*;
import com.mnesa.backend.modules.ai.dto.ExtractedOpportunityDto;
import com.mnesa.backend.modules.ai.dto.ExtractionResultDto;
import com.mnesa.backend.modules.ai.dto.FetchAndExtractRequestDto;
import com.mnesa.backend.modules.ai.repository.AiExtractionRepository;
import com.mnesa.backend.modules.intake.domain.*;
import com.mnesa.backend.modules.intake.repository.CaptureRepository;
import com.mnesa.backend.modules.intake.repository.IntakeJobRepository;
import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IntakeJobProcessor {

    private static final Logger log = LoggerFactory.getLogger(IntakeJobProcessor.class);
    private static final int MAX_ATTEMPTS = 3;

    private final IntakeJobRepository intakeJobRepository;
    private final CaptureRepository captureRepository;
    private final AiServiceClient aiServiceClient;
    private final AiExtractionRepository aiExtractionRepository;
    private final OpportunityRepository opportunityRepository;
    private final ObjectMapper objectMapper;
    private final com.mnesa.backend.modules.ai.repository.AiCandidateRepository aiCandidateRepository;

    public IntakeJobProcessor(IntakeJobRepository intakeJobRepository,
                              CaptureRepository captureRepository,
                              AiServiceClient aiServiceClient,
                              AiExtractionRepository aiExtractionRepository,
                              OpportunityRepository opportunityRepository,
                              ObjectMapper objectMapper) {
        this(intakeJobRepository, captureRepository, aiServiceClient, aiExtractionRepository, opportunityRepository, objectMapper, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public IntakeJobProcessor(IntakeJobRepository intakeJobRepository,
                              CaptureRepository captureRepository,
                              AiServiceClient aiServiceClient,
                              AiExtractionRepository aiExtractionRepository,
                              OpportunityRepository opportunityRepository,
                              ObjectMapper objectMapper,
                              com.mnesa.backend.modules.ai.repository.AiCandidateRepository aiCandidateRepository) {

        this.intakeJobRepository = intakeJobRepository;
        this.captureRepository = captureRepository;
        this.aiServiceClient = aiServiceClient;
        this.aiExtractionRepository = aiExtractionRepository;
        this.opportunityRepository = opportunityRepository;
        this.objectMapper = objectMapper;
        this.aiCandidateRepository = aiCandidateRepository;
    }


    /**
     * Claims and processes a single pending job.
     */
    @Transactional
    public boolean processNextPendingJob() {
        List<IntakeJob> pendingJobs = intakeJobRepository.findTop5ByStatusOrderByCreatedAtAsc(IntakeJobStatus.PENDING);
        if (pendingJobs.isEmpty()) {
            return false;
        }

        IntakeJob job = pendingJobs.get(0);
        processJob(job);
        return true;
    }

    @Transactional
    public void processJob(IntakeJob job) {
        log.info("Processing intake job [{}] for user [{}] (attempt {}/{})",
                job.getId(), job.getUserId(), job.getAttemptCount() + 1, MAX_ATTEMPTS);

        job.setStatus(IntakeJobStatus.PROCESSING);
        job.setAttemptCount(job.getAttemptCount() + 1);
        intakeJobRepository.save(job);

        Optional<Capture> captureOpt = captureRepository.findById(job.getCaptureId());
        if (captureOpt.isEmpty()) {
            log.error("Capture [{}] not found for job [{}]", job.getCaptureId(), job.getId());
            job.setStatus(IntakeJobStatus.FAILED);
            job.setErrorMessage("Associated capture record not found");
            intakeJobRepository.save(job);
            return;
        }

        Capture capture = captureOpt.get();
        capture.setStatus(CaptureStatus.PROCESSING);
        captureRepository.save(capture);

        // Branch 1: Screenshot / Image OCR extraction
        if (capture.getSourceType() == SourceType.IMAGE && capture.getMetadata() != null && !capture.getMetadata().isBlank()) {
            com.mnesa.backend.modules.ai.dto.ImageExtractionRequestDto imgReq =
                    com.mnesa.backend.modules.ai.dto.ImageExtractionRequestDto.builder()
                            .imageBase64(capture.getMetadata())
                            .imageMimeType(capture.getMediaMimeType() != null ? capture.getMediaMimeType() : "image/png")
                            .userNotes(capture.getOriginalText())
                            .sourceUrl(capture.getCanonicalUrl())
                            .build();

            long startTime = System.currentTimeMillis();
            com.mnesa.backend.modules.ai.dto.MultiCandidateExtractionResultDto imgResult = aiServiceClient.extractImage(imgReq);
            long duration = System.currentTimeMillis() - startTime;

            if (imgResult.isSuccess() && (imgResult.getPrimaryOpportunity() != null || (imgResult.getCandidates() != null && !imgResult.getCandidates().isEmpty()))) {
                ExtractedOpportunityDto primary = imgResult.getPrimaryOpportunity() != null
                        ? imgResult.getPrimaryOpportunity()
                        : imgResult.getCandidates().get(0);

                ExtractionResultDto extractionResult = ExtractionResultDto.builder()
                        .success(true)
                        .opportunity(primary)
                        .providerUsed(imgResult.getProviderUsed())
                        .latencyMs(imgResult.getLatencyMs())
                        .sanitizationFlags(imgResult.getSanitizationFlags())
                        .validationStatus(imgResult.getValidationStatus())
                        .build();

                AiExtraction extraction = handleSuccessfulExtraction(job, capture, extractionResult, duration);

                if (aiCandidateRepository != null && imgResult.getCandidates() != null && !imgResult.getCandidates().isEmpty()) {
                    int idx = 0;
                    for (ExtractedOpportunityDto cDto : imgResult.getCandidates()) {
                        String cTitle = (cDto.getTitle() != null && cDto.getTitle().getValue() != null)
                                ? cDto.getTitle().getValue()
                                : "Opportunity Proposal";
                        String cOrg = cDto.getOrganization() != null ? cDto.getOrganization().getValue() : null;
                        String cCat = (cDto.getCategory() != null && cDto.getCategory().getValue() != null)
                                ? cDto.getCategory().getValue()
                                : "OTHER";
                        Instant cDeadline = cDto.getDeadline() != null ? cDto.getDeadline().getValue() : null;
                        String cDeadlineRaw = cDto.getDeadline() != null ? cDto.getDeadline().getRawText() : null;

                        aiCandidateRepository.save(com.mnesa.backend.modules.ai.domain.AiCandidate.builder()
                                .extractionId(extraction.getId())
                                .candidateIndex(idx++)
                                .title(cTitle)
                                .organization(cOrg)
                                .category(cCat)
                                .summary(cDto.getSummary())
                                .deadlineAt(cDeadline)
                                .deadlineRaw(cDeadlineRaw)
                                .confidenceScore(BigDecimal.valueOf(cDto.getOverallConfidence() != null ? cDto.getOverallConfidence() : 0.8))
                                .build());
                    }
                }
                return;
            } else {
                ExtractionResultDto failDto = ExtractionResultDto.builder()
                        .success(false)
                        .errorMessage(imgResult.getErrorMessage() != null ? imgResult.getErrorMessage() : "OCR image extraction failed")
                        .validationStatus(imgResult.getValidationStatus())
                        .build();
                handleFailedExtraction(job, capture, failDto);
                return;
            }
        }

        // Branch 2: Web URL / Text extraction
        FetchAndExtractRequestDto request = FetchAndExtractRequestDto.builder()
                .url(capture.getCanonicalUrl() != null ? capture.getCanonicalUrl() : capture.getOriginalUrl())
                .rawText(capture.getOriginalText())
                .build();

        long startTime = System.currentTimeMillis();
        ExtractionResultDto extractionResult = aiServiceClient.extractOpportunity(request);
        long duration = System.currentTimeMillis() - startTime;

        if (extractionResult.isSuccess() && extractionResult.getOpportunity() != null) {
            handleSuccessfulExtraction(job, capture, extractionResult, duration);
        } else {
            handleFailedExtraction(job, capture, extractionResult);
        }
    }

    private AiExtraction handleSuccessfulExtraction(IntakeJob job, Capture capture, ExtractionResultDto result, long duration) {

        ExtractedOpportunityDto opp = result.getOpportunity();

        String title = (opp.getTitle() != null && opp.getTitle().getValue() != null && !opp.getTitle().getValue().isBlank())
                ? opp.getTitle().getValue()
                : (capture.getSourceDomain() != null ? "Opportunity from " + capture.getSourceDomain() : "Captured Opportunity");

        String organization = opp.getOrganization() != null ? opp.getOrganization().getValue() : null;

        String category = "OTHER";
        if (opp.getCategory() != null && opp.getCategory().getValue() != null) {
            category = opp.getCategory().getValue().toUpperCase();
        }

        Instant deadlineAt = opp.getDeadline() != null ? opp.getDeadline().getValue() : null;
        String deadlineRaw = opp.getDeadline() != null ? opp.getDeadline().getRawText() : null;
        String deadlineTz = opp.getDeadline() != null ? opp.getDeadline().getTimezone() : null;
        boolean deadlineAmbiguous = opp.getDeadline() != null && opp.getDeadline().isAmbiguous();

        ValidationStatus validationStatus = ValidationStatus.SUCCEEDED;
        if (opp.getValidationStatus() != null) {
            try {
                validationStatus = ValidationStatus.valueOf(opp.getValidationStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        PriorityLevel priority = PriorityLevel.NORMAL;
        if (opp.getPriority() != null) {
            try {
                priority = PriorityLevel.valueOf(opp.getPriority().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        WorkMode workMode = WorkMode.UNKNOWN;
        if (opp.getWorkMode() != null) {
            try {
                workMode = WorkMode.valueOf(opp.getWorkMode().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        String location = opp.getLocation() != null ? opp.getLocation().getValue() : null;
        String eligibility = opp.getEligibility() != null ? opp.getEligibility().getValue() : null;

        String extractedFieldsJson = null;
        String evidenceSnippetsJson = null;
        String warningsJson = null;
        try {
            extractedFieldsJson = objectMapper.writeValueAsString(opp);
            evidenceSnippetsJson = objectMapper.writeValueAsString(opp.getEvidenceSnippets());
            warningsJson = objectMapper.writeValueAsString(opp.getWarningMessages());
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize extracted fields JSON: {}", e.getMessage());
        }

        // 1. Persist AiExtraction proposal
        AiExtraction extraction = AiExtraction.builder()
                .captureId(capture.getId())
                .intakeJobId(job.getId())
                .userId(job.getUserId())
                .provider(result.getProviderUsed() != null ? result.getProviderUsed() : "unknown")
                .modelName("gemini-2.5-flash")
                .schemaVersion("v1")
                .validationStatus(validationStatus)
                .overallConfidence(BigDecimal.valueOf(opp.getOverallConfidence() != null ? opp.getOverallConfidence() : 0.8))
                .title(title)
                .organization(organization)
                .category(category)
                .summary(opp.getSummary())
                .deadlineAt(deadlineAt)
                .deadlineRaw(deadlineRaw)
                .deadlineTimezone(deadlineTz)
                .deadlineAmbiguous(deadlineAmbiguous)
                .registrationUrl(opp.getRegistrationUrl() != null ? opp.getRegistrationUrl() : capture.getCanonicalUrl())
                .location(location)
                .workMode(workMode)
                .eligibility(eligibility)
                .estimatedEffortMinutes(opp.getEstimatedEffortMinutes())
                .priority(priority)
                .priorityReason(opp.getPriorityReason())
                .extractedFields(extractedFieldsJson)
                .evidenceSnippets(evidenceSnippetsJson)
                .warningMessages(warningsJson)
                .processingDurationMs(duration)
                .build();
        aiExtractionRepository.save(extraction);

        // 2. Transition IntakeJob and Capture to COMPLETED
        job.setStatus(IntakeJobStatus.COMPLETED);
        job.setErrorMessage(null);
        intakeJobRepository.save(job);

        capture.setStatus(CaptureStatus.COMPLETED);
        captureRepository.save(capture);

        // 3. Create or update initial Opportunity in UNDERSTOOD state (awaiting user confirmation)
        OpportunityType oppType = OpportunityType.OTHER;
        try {
            oppType = OpportunityType.valueOf(category);
        } catch (IllegalArgumentException ignored) {}

        Opportunity opportunity = Opportunity.builder()
                .userId(job.getUserId())
                .title(title)
                .organization(organization)
                .opportunityType(oppType)
                .description(opp.getSummary())
                .sourceUrl(capture.getCanonicalUrl() != null ? capture.getCanonicalUrl() : capture.getOriginalUrl())
                .registrationUrl(opp.getRegistrationUrl() != null ? opp.getRegistrationUrl() : capture.getCanonicalUrl())
                .sourceDomain(capture.getSourceDomain())
                .rawContent(capture.getOriginalText())
                .status(OpportunityStatus.UNDERSTOOD)
                .deadlineAt(deadlineAt)
                .deadlineTimezone(deadlineTz)
                .location(location)
                .workMode(workMode != null ? workMode.name() : "UNSPECIFIED")
                .eligibility(eligibility)
                .estimatedEffort(opp.getEstimatedEffortMinutes() != null ? opp.getEstimatedEffortMinutes() + " mins" : null)
                .priority(priority != null ? priority.name() : "NORMAL")
                .priorityReason(opp.getPriorityReason())
                .confidenceScore(BigDecimal.valueOf(opp.getOverallConfidence() != null ? opp.getOverallConfidence() : 0.8))
                .extractionId(extraction.getId())
                .build();
        opportunityRepository.save(opportunity);

        log.info("Intake job [{}] completed successfully. Saved AiExtraction [{}] and Opportunity [{}]",
                job.getId(), extraction.getId(), opportunity.getId());
        return extraction;
    }


    private void handleFailedExtraction(IntakeJob job, Capture capture, ExtractionResultDto result) {
        String errMsg = result.getErrorMessage() != null ? result.getErrorMessage() : "Unknown AI extraction error";
        log.warn("Intake job [{}] failed on attempt {}: {}", job.getId(), job.getAttemptCount(), errMsg);

        if (job.getAttemptCount() >= MAX_ATTEMPTS) {
            job.setStatus(IntakeJobStatus.FAILED);
            job.setErrorMessage(errMsg);
            capture.setStatus(CaptureStatus.FAILED);
            captureRepository.save(capture);
        } else {
            job.setStatus(IntakeJobStatus.PENDING); // Retryable
            job.setErrorMessage(errMsg);
        }
        intakeJobRepository.save(job);
    }
}
