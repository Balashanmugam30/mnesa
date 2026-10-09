package com.mnesa.android.presentation.opportunities;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.data.remote.dto.OpportunityActivityDto;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Completable;
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
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OpportunityDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private OpportunityRepository opportunityRepository;

    private OpportunityDetailViewModel viewModel;

    private Opportunity testOpportunity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        testOpportunity = new Opportunity(
                "opp-100",
                "user-1",
                "Google Fellowship 2026",
                "Google",
                OpportunityType.INTERNSHIP,
                "INTERNSHIP",
                OpportunityStatus.SAVED,
                "Description here",
                "https://careers.google.com",
                "https://careers.google.com/apply",
                System.currentTimeMillis() + 86400000L,
                "UTC",
                "Open to all students",
                "Mountain View, CA",
                "10 hours/week",
                "HIGH",
                0.95f,
                1000L,
                1000L,
                "SYNCED"
        );

        when(opportunityRepository.getOpportunityById("opp-100", "user-1"))
                .thenReturn(Single.just(testOpportunity));
        when(opportunityRepository.getOpportunityHistory("opp-100"))
                .thenReturn(Single.just(Collections.emptyList()));

        viewModel = new OpportunityDetailViewModel(opportunityRepository, "user-1");
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testLoadOpportunitySuccess() {
        viewModel.loadOpportunity("opp-100");

        assertNotNull(viewModel.getOpportunity().getValue());
        assertEquals("Google Fellowship 2026", viewModel.getOpportunity().getValue().getTitle());
        assertEquals("Google", viewModel.getOpportunity().getValue().getOrganization());
        assertEquals(OpportunityStatus.SAVED, viewModel.getOpportunity().getValue().getStatus());
        assertEquals(Boolean.FALSE, viewModel.getIsLoading().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testLoadOpportunityError() {
        when(opportunityRepository.getOpportunityById("bad-id", "user-1"))
                .thenReturn(Single.error(new RuntimeException("Opportunity not found")));

        viewModel.loadOpportunity("bad-id");

        assertEquals(Boolean.FALSE, viewModel.getIsLoading().getValue());
        assertEquals("Opportunity not found", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testUpdateStatusSuccess() {
        when(opportunityRepository.updateStatus("opp-100", "APPLYING", "Applying now", "user-1"))
                .thenReturn(Completable.complete());

        viewModel.loadOpportunity("opp-100");
        viewModel.updateStatus("APPLYING", "Applying now");

        verify(opportunityRepository).updateStatus("opp-100", "APPLYING", "Applying now", "user-1");
        assertNotNull(viewModel.getActionSuccess().getValue());
        assertTrue(viewModel.getActionSuccess().getValue().contains("APPLYING"));
    }

    @Test
    public void testArchiveOpportunitySuccess() {
        when(opportunityRepository.archiveOpportunity("opp-100", "user-1"))
                .thenReturn(Completable.complete());

        viewModel.loadOpportunity("opp-100");
        viewModel.archiveOpportunity();

        verify(opportunityRepository).archiveOpportunity("opp-100", "user-1");
        assertNotNull(viewModel.getActionSuccess().getValue());
        assertTrue(viewModel.getActionSuccess().getValue().contains("archived"));
    }

    @Test
    public void testRestoreOpportunitySuccess() {
        when(opportunityRepository.restoreOpportunity("opp-100", "user-1"))
                .thenReturn(Completable.complete());

        viewModel.loadOpportunity("opp-100");
        viewModel.restoreOpportunity();

        verify(opportunityRepository).restoreOpportunity("opp-100", "user-1");
        assertNotNull(viewModel.getActionSuccess().getValue());
        assertTrue(viewModel.getActionSuccess().getValue().contains("restored"));
    }

    @Test
    public void testDeleteOpportunitySuccess() {
        when(opportunityRepository.deleteOpportunity("opp-100", "user-1"))
                .thenReturn(Completable.complete());

        viewModel.loadOpportunity("opp-100");
        viewModel.deleteOpportunity();

        verify(opportunityRepository).deleteOpportunity("opp-100", "user-1");
        assertNotNull(viewModel.getActionSuccess().getValue());
        assertTrue(viewModel.getActionSuccess().getValue().contains("deleted"));
    }
}
