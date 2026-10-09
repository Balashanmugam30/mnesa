package com.mnesa.backend.modules.intake.service;

import com.mnesa.backend.common.exception.MnesaException;
import com.mnesa.backend.modules.intake.domain.*;
import com.mnesa.backend.modules.intake.dto.IntakeRequest;
import com.mnesa.backend.modules.intake.dto.IntakeResponse;
import com.mnesa.backend.modules.intake.repository.CaptureRepository;
import com.mnesa.backend.modules.intake.repository.IntakeJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntakeServiceTest {

    @Mock
    private CaptureRepository captureRepository;

    @Mock
    private IntakeJobRepository intakeJobRepository;

    @Mock
    private UrlSanitizerService urlSanitizerService;

    @InjectMocks
    private IntakeService intakeService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should successfully ingest new URL share intake and persist Capture and IntakeJob")
    void testProcessIntakeUrlSuccess() {
        String idempotencyKey = "key-12345";
        String rawUrl = "https://example.com/internship";
        String canonicalUrl = "https://example.com/internship";
        IntakeRequest request = IntakeRequest.builder()
                .idempotencyKey(idempotencyKey)
                .url(rawUrl)
                .build();

        when(captureRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey))
                .thenReturn(Optional.empty());
        when(urlSanitizerService.sanitizeAndNormalize(rawUrl))
                .thenReturn(canonicalUrl);
        when(urlSanitizerService.extractDomain(canonicalUrl))
                .thenReturn("example.com");

        UUID captureId = UUID.randomUUID();
        Capture savedCapture = Capture.builder()
                .id(captureId)
                .userId(userId)
                .sourceType(SourceType.URL)
                .canonicalUrl(canonicalUrl)
                .status(CaptureStatus.RECEIVED)
                .createdAt(Instant.now())
                .build();
        when(captureRepository.save(any(Capture.class))).thenReturn(savedCapture);

        UUID jobId = UUID.randomUUID();
        IntakeJob savedJob = IntakeJob.builder()
                .id(jobId)
                .captureId(captureId)
                .userId(userId)
                .status(IntakeJobStatus.PENDING)
                .build();
        when(intakeJobRepository.save(any(IntakeJob.class))).thenReturn(savedJob);

        IntakeResponse response = intakeService.processIntake(userId, request);

        assertNotNull(response);
        assertEquals(captureId, response.getCaptureId());
        assertEquals(jobId, response.getJobId());
        assertEquals("RECEIVED", response.getStatus());
        assertEquals("URL", response.getSourceType());
        assertFalse(response.isDuplicate());

        verify(captureRepository, times(1)).save(any(Capture.class));
        verify(intakeJobRepository, times(1)).save(any(IntakeJob.class));
    }

    @Test
    @DisplayName("Should return existing capture when matching idempotencyKey is encountered")
    void testProcessIntakeIdempotentReplay() {
        String idempotencyKey = "key-replay";
        IntakeRequest request = IntakeRequest.builder()
                .idempotencyKey(idempotencyKey)
                .text("Opportunity description")
                .build();

        UUID captureId = UUID.randomUUID();
        Capture existingCapture = Capture.builder()
                .id(captureId)
                .userId(userId)
                .sourceType(SourceType.TEXT)
                .status(CaptureStatus.RECEIVED)
                .createdAt(Instant.now())
                .build();

        UUID jobId = UUID.randomUUID();
        IntakeJob existingJob = IntakeJob.builder()
                .id(jobId)
                .captureId(captureId)
                .userId(userId)
                .status(IntakeJobStatus.PENDING)
                .build();

        when(captureRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey))
                .thenReturn(Optional.of(existingCapture));
        when(intakeJobRepository.findByCaptureId(captureId))
                .thenReturn(Optional.of(existingJob));

        IntakeResponse response = intakeService.processIntake(userId, request);

        assertNotNull(response);
        assertTrue(response.isDuplicate());
        assertEquals(captureId, response.getCaptureId());
        assertEquals(jobId, response.getJobId());

        verify(captureRepository, never()).save(any(Capture.class));
        verify(intakeJobRepository, never()).save(any(IntakeJob.class));
    }

    @Test
    @DisplayName("Should throw MnesaException when intake payload is completely empty")
    void testProcessIntakeEmptyPayloadThrows() {
        IntakeRequest request = IntakeRequest.builder()
                .idempotencyKey("key-empty")
                .build();

        assertThrows(MnesaException.class, () -> intakeService.processIntake(userId, request));
    }
}
