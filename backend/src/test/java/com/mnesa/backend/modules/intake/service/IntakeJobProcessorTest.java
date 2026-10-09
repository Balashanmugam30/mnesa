package com.mnesa.backend.modules.intake.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.ai.client.AiServiceClient;
import com.mnesa.backend.modules.ai.domain.AiExtraction;
import com.mnesa.backend.modules.ai.dto.*;
import com.mnesa.backend.modules.ai.repository.AiExtractionRepository;
import com.mnesa.backend.modules.intake.domain.*;
import com.mnesa.backend.modules.intake.repository.CaptureRepository;
import com.mnesa.backend.modules.intake.repository.IntakeJobRepository;
import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntakeJobProcessorTest {

    @Mock
    private IntakeJobRepository intakeJobRepository;

    @Mock
    private CaptureRepository captureRepository;

    @Mock
    private AiServiceClient aiServiceClient;

    @Mock
    private AiExtractionRepository aiExtractionRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    private IntakeJobProcessor processor;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        processor = new IntakeJobProcessor(
                intakeJobRepository,
                captureRepository,
                aiServiceClient,
                aiExtractionRepository,
                opportunityRepository,
                objectMapper
        );
    }

    @Test
    @DisplayName("processJob successfully invokes AI service and persists extraction record & understood opportunity")
    void processJobSuccess() {
        UUID userId = UUID.randomUUID();
        UUID captureId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();

        IntakeJob job = IntakeJob.builder()
                .id(jobId)
                .userId(userId)
                .captureId(captureId)
                .status(IntakeJobStatus.PENDING)
                .attemptCount(0)
                .build();

        Capture capture = Capture.builder()
                .id(captureId)
                .userId(userId)
                .canonicalUrl("https://example.com/hackathon")
                .originalText("Join MIT Hackathon 2026!")
                .sourceType(SourceType.URL)
                .status(CaptureStatus.RECEIVED)
                .build();

        when(captureRepository.findById(captureId)).thenReturn(Optional.of(capture));

        ExtractedOpportunityDto oppDto = ExtractedOpportunityDto.builder()
                .title(FieldResultDto.<String>builder().value("MIT Hackathon 2026").confidence(0.95).build())
                .organization(FieldResultDto.<String>builder().value("MIT").confidence(0.90).build())
                .category(FieldResultDto.<String>builder().value("HACKATHON").confidence(0.98).build())
                .summary("Annual student hackathon.")
                .deadline(DeadlineDto.builder().value(Instant.now().plusSeconds(86400 * 7)).confidence(0.9).build())
                .overallConfidence(0.94)
                .validationStatus("SUCCEEDED")
                .priority("HIGH")
                .build();

        ExtractionResultDto aiResult = ExtractionResultDto.builder()
                .success(true)
                .providerUsed("gemini")
                .opportunity(oppDto)
                .build();

        when(aiServiceClient.extractOpportunity(any(FetchAndExtractRequestDto.class))).thenReturn(aiResult);

        processor.processJob(job);

        // Verify AiExtraction saved
        ArgumentCaptor<AiExtraction> extractionCaptor = ArgumentCaptor.forClass(AiExtraction.class);
        verify(aiExtractionRepository).save(extractionCaptor.capture());
        AiExtraction savedExtraction = extractionCaptor.getValue();
        assertEquals("MIT Hackathon 2026", savedExtraction.getTitle());
        assertEquals("HACKATHON", savedExtraction.getCategory());
        assertEquals(userId, savedExtraction.getUserId());

        // Verify Opportunity saved with UNDERSTOOD state
        ArgumentCaptor<Opportunity> oppCaptor = ArgumentCaptor.forClass(Opportunity.class);
        verify(opportunityRepository).save(oppCaptor.capture());
        Opportunity savedOpp = oppCaptor.getValue();
        assertEquals("MIT Hackathon 2026", savedOpp.getTitle());
        assertEquals(OpportunityStatus.UNDERSTOOD, savedOpp.getStatus());

        // Verify job marked COMPLETED
        assertEquals(IntakeJobStatus.COMPLETED, job.getStatus());
        assertNull(job.getErrorMessage());
    }

    @Test
    @DisplayName("processJob on AI service failure records error and retries or fails permanently after 3 attempts")
    void processJobFailureHandling() {
        UUID userId = UUID.randomUUID();
        UUID captureId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();

        IntakeJob job = IntakeJob.builder()
                .id(jobId)
                .userId(userId)
                .captureId(captureId)
                .status(IntakeJobStatus.PENDING)
                .attemptCount(2) // 3rd attempt
                .build();

        Capture capture = Capture.builder()
                .id(captureId)
                .userId(userId)
                .canonicalUrl("https://broken.example.com")
                .status(CaptureStatus.RECEIVED)
                .build();

        when(captureRepository.findById(captureId)).thenReturn(Optional.of(capture));

        ExtractionResultDto aiResult = ExtractionResultDto.builder()
                .success(false)
                .errorMessage("Webpage unavailable (HTTP 404)")
                .build();

        when(aiServiceClient.extractOpportunity(any(FetchAndExtractRequestDto.class))).thenReturn(aiResult);

        processor.processJob(job);

        // 3rd attempt fails permanently
        assertEquals(IntakeJobStatus.FAILED, job.getStatus());
        assertEquals(3, job.getAttemptCount());
        assertTrue(job.getErrorMessage().contains("404"));
        assertEquals(CaptureStatus.FAILED, capture.getStatus());
    }
}
