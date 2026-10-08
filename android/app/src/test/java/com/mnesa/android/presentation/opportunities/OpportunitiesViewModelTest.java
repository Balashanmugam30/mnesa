package com.mnesa.android.presentation.opportunities;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OpportunitiesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private OpportunityRepository opportunityRepository;

    private OpportunitiesViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(opportunityRepository.getOpportunitiesByCategory(anyString(), anyString()))
                .thenReturn(Flowable.just(Collections.emptyList()));
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testInitialEmptyLoad() {
        viewModel = new OpportunitiesViewModel(opportunityRepository, "user-456");

        assertEquals(Boolean.FALSE, viewModel.getIsLoading().getValue());
        assertEquals("ALL", viewModel.getActiveFilter().getValue());
        assertNotNull(viewModel.getOpportunities().getValue());
        assertTrue(viewModel.getOpportunities().getValue().isEmpty());
    }

    @Test
    public void testCategoryFiltering() {
        List<Opportunity> internships = new ArrayList<>();
        internships.add(new Opportunity("opp-int", "user-456", "Internship 1", "Stripe",
                OpportunityType.INTERNSHIP, "INTERNSHIP", OpportunityStatus.CAPTURED,
                null, null, null, null, null, null, null, null, "MEDIUM", 0.9f, 1000L, 1000L, "SYNCED"));

        when(opportunityRepository.getOpportunitiesByCategory("user-456", "INTERNSHIP"))
                .thenReturn(Flowable.just(internships));

        viewModel = new OpportunitiesViewModel(opportunityRepository, "user-456");
        viewModel.setCategoryFilter("INTERNSHIP");

        assertEquals("INTERNSHIP", viewModel.getActiveFilter().getValue());
        assertEquals(1, viewModel.getOpportunities().getValue().size());
        assertEquals("Internship 1", viewModel.getOpportunities().getValue().get(0).getTitle());
    }

    @Test
    public void testErrorHandling() {
        when(opportunityRepository.getOpportunitiesByCategory("user-456", "ALL"))
                .thenReturn(Flowable.error(new RuntimeException("Database I/O error")));

        viewModel = new OpportunitiesViewModel(opportunityRepository, "user-456");

        assertEquals(Boolean.FALSE, viewModel.getIsLoading().getValue());
        assertEquals("Database I/O error", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testDeleteOpportunity() {
        when(opportunityRepository.deleteOpportunity("opp-1", "user-456"))
                .thenReturn(Completable.complete());

        viewModel = new OpportunitiesViewModel(opportunityRepository, "user-456");
        viewModel.deleteOpportunity("opp-1");

        verify(opportunityRepository).deleteOpportunity("opp-1", "user-456");
    }
}
