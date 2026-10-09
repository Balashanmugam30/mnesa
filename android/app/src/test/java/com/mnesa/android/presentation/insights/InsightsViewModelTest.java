package com.mnesa.android.presentation.insights;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.data.remote.dto.ActivityTrendPointDto;
import com.mnesa.android.data.remote.dto.InsightsResponseDto;
import com.mnesa.android.domain.repository.InsightsRepository;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InsightsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private InsightsRepository insightsRepository;

    private InsightsViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testLoadInsightsSuccess() {
        InsightsResponseDto dto = new InsightsResponseDto();
        dto.setTotalSaved(10);
        dto.setApplicationsCompleted(4);
        dto.setUpcomingDeadlines(3);
        dto.setMissedOpportunities(1);

        Map<String, Long> catDist = new HashMap<>();
        catDist.put("INTERNSHIP", 6L);
        catDist.put("HACKATHON", 4L);
        dto.setCategoryDistribution(catDist);

        ActivityTrendPointDto trendPoint = new ActivityTrendPointDto("2026-10-09", 5);
        dto.setActivityTrends(Collections.singletonList(trendPoint));

        when(insightsRepository.getInsights()).thenReturn(Single.just(dto));

        viewModel = new InsightsViewModel(insightsRepository);

        verify(insightsRepository).getInsights();

        InsightsUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertFalse(state.isLoading());
        assertFalse(state.isError());
        assertEquals(10L, state.getTotalSaved());
        assertEquals(4L, state.getApplicationsCompleted());
        assertEquals(3L, state.getUpcomingDeadlines());
        assertEquals(1L, state.getMissedOpportunities());
        assertEquals(2, state.getCategoryDistribution().size());
        assertEquals(1, state.getActivityTrends().size());
    }

    @Test
    public void testLoadInsightsError() {
        when(insightsRepository.getInsights())
                .thenReturn(Single.error(new RuntimeException("Server error 500")));

        viewModel = new InsightsViewModel(insightsRepository);

        InsightsUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertFalse(state.isLoading());
        assertTrue(state.isError());
        assertEquals("Server error 500", state.getErrorMessage());
    }
}
