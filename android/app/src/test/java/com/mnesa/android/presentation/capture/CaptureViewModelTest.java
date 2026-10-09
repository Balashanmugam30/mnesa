package com.mnesa.android.presentation.capture;

import android.content.Intent;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.data.remote.dto.IntakeResponseDto;
import com.mnesa.android.domain.IntakePayloadParser;
import com.mnesa.android.domain.model.CaptureState;
import com.mnesa.android.domain.model.IntakePayload;
import com.mnesa.android.domain.repository.IntakeRepository;
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CaptureViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private IntakeRepository repository;

    @Mock
    private IntakePayloadParser parser;

    @Mock
    private Intent intent;

    private CaptureViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        viewModel = new CaptureViewModel(repository, parser);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testProcessIncomingIntentSuccessfulIntakeAcknowledgedWithoutJob() {
        IntakePayload validPayload = new IntakePayload(
                "https://careers.google.com/jobs/123",
                "https://careers.google.com/jobs/123",
                "careers.google.com",
                "URL",
                null,
                "text/plain",
                0,
                true,
                null
        );

        IntakeResponseDto responseDto = new IntakeResponseDto();
        responseDto.setCaptureId("cap-123");
        responseDto.setJobId(null);
        responseDto.setStatus("RECEIVED");
        responseDto.setMessage("Opportunity captured and queued");
        responseDto.setDuplicate(false);

        when(parser.parse(any(), any())).thenReturn(validPayload);
        when(repository.processAndSubmit(eq(validPayload), any())).thenReturn(Single.just(responseDto));

        viewModel.processIncomingIntent(intent, null);

        CaptureUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertEquals(CaptureState.ACKNOWLEDGED, state.getState());
        assertTrue(state.isSuccess());
        assertFalse(state.isDuplicate());
        assertEquals("Opportunity Captured!", state.getSubtitle());
    }

    @Test
    public void testProcessIncomingIntentWithJobStartsAnalyzing() {
        IntakePayload validPayload = new IntakePayload(
                "https://careers.google.com/jobs/123",
                "https://careers.google.com/jobs/123",
                "careers.google.com",
                "URL",
                null,
                "text/plain",
                0,
                true,
                null
        );

        IntakeResponseDto responseDto = new IntakeResponseDto();
        responseDto.setCaptureId("cap-123");
        responseDto.setJobId("job-456");
        responseDto.setStatus("RECEIVED");
        responseDto.setMessage("Opportunity captured and queued");
        responseDto.setDuplicate(false);

        when(parser.parse(any(), any())).thenReturn(validPayload);
        when(repository.processAndSubmit(eq(validPayload), any())).thenReturn(Single.just(responseDto));
        when(repository.pollJobStatus(eq("job-456"))).thenReturn(Single.never());

        viewModel.processIncomingIntent(intent, null);

        CaptureUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertEquals(CaptureState.ANALYZING, state.getState());
        assertEquals("Analyzing with MNESA AI...", state.getSubtitle());
        assertTrue(state.isProgressVisible());
    }

    @Test
    public void testProcessIncomingIntentDuplicateIntake() {
        IntakePayload validPayload = new IntakePayload(
                "https://example.com/hackathon",
                "https://example.com/hackathon",
                "example.com",
                "URL",
                null,
                "text/plain",
                0,
                true,
                null
        );

        IntakeResponseDto responseDto = new IntakeResponseDto();
        responseDto.setCaptureId("cap-123");
        responseDto.setJobId("job-456");
        responseDto.setStatus("RECEIVED");
        responseDto.setMessage("Previously captured opportunity");
        responseDto.setDuplicate(true);

        when(parser.parse(any(), any())).thenReturn(validPayload);
        when(repository.processAndSubmit(eq(validPayload), any())).thenReturn(Single.just(responseDto));

        viewModel.processIncomingIntent(intent, null);

        CaptureUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertEquals(CaptureState.DUPLICATE, state.getState());
        assertTrue(state.isDuplicate());
        assertEquals("Previously Captured", state.getSubtitle());
    }

    @Test
    public void testProcessIncomingIntentNetworkFailureFallsBackToRetryable() {
        IntakePayload validPayload = new IntakePayload(
                "https://example.com/grant",
                "https://example.com/grant",
                "example.com",
                "URL",
                null,
                "text/plain",
                0,
                true,
                null
        );

        when(parser.parse(any(), any())).thenReturn(validPayload);
        when(repository.processAndSubmit(eq(validPayload), any())).thenReturn(Single.error(new IOException("Connection reset")));

        viewModel.processIncomingIntent(intent, null);

        CaptureUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertEquals(CaptureState.RETRYABLE_FAILURE, state.getState());
        assertTrue(state.isOffline());
        assertTrue(state.canRetry());
    }

    @Test
    public void testProcessIncomingIntentInvalidPayloadSetsUnsupported() {
        IntakePayload invalidPayload = new IntakePayload(
                null, null, null, "UNSUPPORTED", null, null, 0, false, "Empty payload"
        );

        when(parser.parse(any(), any())).thenReturn(invalidPayload);

        viewModel.processIncomingIntent(intent, null);

        CaptureUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertEquals(CaptureState.UNSUPPORTED, state.getState());
        assertTrue(state.isError());
    }

    @Test
    public void testConfirmOpportunitySetsConfirmedState() {
        IntakePayload validPayload = new IntakePayload(
                "https://careers.google.com/jobs/123",
                "https://careers.google.com/jobs/123",
                "careers.google.com",
                "URL",
                null,
                "text/plain",
                0,
                true,
                null
        );

        IntakeResponseDto responseDto = new IntakeResponseDto();
        responseDto.setCaptureId("cap-123");
        responseDto.setJobId("job-456");
        responseDto.setStatus("RECEIVED");
        responseDto.setMessage("Opportunity captured and queued");
        responseDto.setDuplicate(false);

        when(parser.parse(any(), any())).thenReturn(validPayload);
        when(repository.processAndSubmit(eq(validPayload), any())).thenReturn(Single.just(responseDto));
        when(repository.pollJobStatus(eq("job-456"))).thenReturn(Single.never());
        when(repository.confirmOpportunity(eq("job-456"), any())).thenReturn(Single.just(true));

        viewModel.processIncomingIntent(intent, null);
        viewModel.confirmOpportunity("Software Engineering Intern", "INTERNSHIP", "2026-11-01");

        CaptureUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertEquals(CaptureState.CONFIRMED, state.getState());
        assertEquals("Software Engineering Intern", state.getTitle());
        assertEquals("INTERNSHIP", state.getCategory());
        assertTrue(state.isSuccess());
    }
}
