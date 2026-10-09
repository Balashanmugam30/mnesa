package com.mnesa.android.data.repository;

import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.dao.CaptureDao;
import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.CaptureEntity;
import com.mnesa.android.data.remote.api.IntakeApiService;
import com.mnesa.android.data.remote.dto.ApiResponseDto;
import com.mnesa.android.data.remote.dto.IntakeResponseDto;
import com.mnesa.android.domain.model.IntakePayload;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IntakeRepositoryTest {

    @Mock
    private CaptureDao captureDao;

    @Mock
    private OpportunityDao opportunityDao;

    @Mock
    private SyncQueueDao syncQueueDao;

    @Mock
    private IntakeApiService apiService;

    @Mock
    private SecureTokenManager tokenManager;

    private IntakeRepositoryImpl repository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(tokenManager.getUserId()).thenReturn("user-test-uuid");
        repository = new IntakeRepositoryImpl(null, captureDao, opportunityDao, syncQueueDao, apiService, tokenManager);
    }

    @Test
    public void testSaveOfflineCapturePersistsCaptureAndSyncQueue() {
        when(captureDao.insert(any())).thenReturn(Completable.complete());
        when(opportunityDao.insertOpportunity(any())).thenReturn(Completable.complete());
        when(syncQueueDao.enqueue(any())).thenReturn(Completable.complete());

        IntakePayload payload = new IntakePayload(
                "Notes from hackathon kickoff",
                null,
                null,
                "TEXT",
                null,
                "text/plain",
                0,
                true,
                null
        );

        CaptureEntity result = repository.saveOfflineCapture(payload, "idem-test-1").blockingGet();

        assertNotNull(result);
        assertEquals("user-test-uuid", result.getUserId());
        assertEquals("QUEUED", result.getStatus());
        assertEquals("TEXT", result.getSourceType());

        verify(captureDao, times(1)).insert(any());
        verify(opportunityDao, times(1)).insertOpportunity(any());
        verify(syncQueueDao, times(1)).enqueue(any());
    }

    @Test
    public void testProcessAndSubmitSuccessfulOnlineSubmission() {
        when(captureDao.insert(any())).thenReturn(Completable.complete());
        when(opportunityDao.insertOpportunity(any())).thenReturn(Completable.complete());
        when(captureDao.update(any())).thenReturn(Completable.complete());
        when(opportunityDao.updateOpportunity(any())).thenReturn(Completable.complete());

        IntakeResponseDto responseDto = new IntakeResponseDto();
        responseDto.setCaptureId("cap-server-id");
        responseDto.setJobId("job-server-id");
        responseDto.setStatus("RECEIVED");
        responseDto.setCanonicalUrl("https://example.com/clean");
        responseDto.setDuplicate(false);

        ApiResponseDto<IntakeResponseDto> apiResponse = new ApiResponseDto<>();
        apiResponse.setSuccess(true);
        apiResponse.setData(responseDto);

        when(apiService.submitIntake(any())).thenReturn(Single.just(apiResponse));

        IntakePayload payload = new IntakePayload(
                "https://example.com/job?utm_source=test",
                "https://example.com/job?utm_source=test",
                "example.com",
                "URL",
                null,
                "text/plain",
                0,
                true,
                null
        );

        IntakeResponseDto result = repository.processAndSubmit(payload, "idem-test-2").blockingGet();

        assertNotNull(result);
        assertEquals("cap-server-id", result.getCaptureId());
        assertEquals("job-server-id", result.getJobId());

        verify(captureDao, times(1)).insert(any());
        verify(captureDao, times(1)).update(any());
        verify(opportunityDao, times(1)).insertOpportunity(any());
        verify(opportunityDao, times(1)).updateOpportunity(any());
    }

    @Test
    public void testPollJobStatusSuccess() {
        com.mnesa.android.data.remote.dto.IntakeJobStatusDto statusDto = new com.mnesa.android.data.remote.dto.IntakeJobStatusDto();
        statusDto.setJobId("job-status-123");
        statusDto.setStatus("COMPLETED");

        ApiResponseDto<com.mnesa.android.data.remote.dto.IntakeJobStatusDto> apiResponse = new ApiResponseDto<>();
        apiResponse.setSuccess(true);
        apiResponse.setData(statusDto);

        when(apiService.getJobStatus("job-status-123")).thenReturn(Single.just(apiResponse));

        com.mnesa.android.data.remote.dto.IntakeJobStatusDto result = repository.pollJobStatus("job-status-123").blockingGet();

        assertNotNull(result);
        assertEquals("job-status-123", result.getJobId());
        assertEquals("COMPLETED", result.getStatus());
        verify(apiService, times(1)).getJobStatus("job-status-123");
    }

    @Test
    public void testConfirmOpportunitySuccess() {
        com.mnesa.android.data.remote.dto.ConfirmOpportunityRequestDto requestDto =
                new com.mnesa.android.data.remote.dto.ConfirmOpportunityRequestDto("My Opp", "Google", "INTERNSHIP", null, null);

        ApiResponseDto<Object> apiResponse = new ApiResponseDto<>();
        apiResponse.setSuccess(true);
        apiResponse.setData("Confirmed");

        when(apiService.confirmJob(eq("job-status-123"), any())).thenReturn(Single.just(apiResponse));

        Boolean result = repository.confirmOpportunity("job-status-123", requestDto).blockingGet();

        assertNotNull(result);
        assertTrue(result);
        verify(apiService, times(1)).confirmJob(eq("job-status-123"), any());
    }
}
