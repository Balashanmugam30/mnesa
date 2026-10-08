package com.mnesa.android.presentation.home;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.repository.OpportunityRepository;
import com.mnesa.android.domain.repository.ReminderRepository;
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
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

public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private ReminderRepository reminderRepository;

    private HomeViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(opportunityRepository.getAllOpportunities(anyString())).thenReturn(Flowable.just(Collections.emptyList()));
        when(opportunityRepository.getNeedsAttention(anyString())).thenReturn(Flowable.just(Collections.emptyList()));
        when(opportunityRepository.getUpcoming(anyString())).thenReturn(Flowable.just(Collections.emptyList()));
        when(opportunityRepository.getRecentlySaved(anyString(), anyInt())).thenReturn(Flowable.just(Collections.emptyList()));
        when(reminderRepository.getScheduledCount(anyString())).thenReturn(Single.just(0));
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testEmptyDashboardState() {
        viewModel = new HomeViewModel(opportunityRepository, reminderRepository, "user-123");

        assertEquals(Boolean.TRUE, viewModel.getIsEmpty().getValue());
        assertEquals(Integer.valueOf(0), viewModel.getCountTracked().getValue());
        assertEquals(Integer.valueOf(0), viewModel.getCountUrgent().getValue());
        assertEquals(Integer.valueOf(0), viewModel.getCountReminders().getValue());
    }

    @Test
    public void testPopulatedDashboardState() {
        List<Opportunity> list = new ArrayList<>();
        list.add(new Opportunity("opp-1", "user-123", "AI Hackathon", "Anthropic",
                OpportunityType.HACKATHON, "HACKATHON", OpportunityStatus.CAPTURED,
                "Desc", "url", null, System.currentTimeMillis() + 86400000L,
                "UTC", null, null, null, "HIGH", 0.9f, 1000L, 1000L, "SYNCED"));

        when(opportunityRepository.getAllOpportunities("user-123")).thenReturn(Flowable.just(list));
        when(opportunityRepository.getNeedsAttention("user-123")).thenReturn(Flowable.just(list));
        when(reminderRepository.getScheduledCount("user-123")).thenReturn(Single.just(2));

        viewModel = new HomeViewModel(opportunityRepository, reminderRepository, "user-123");

        assertEquals(Boolean.FALSE, viewModel.getIsEmpty().getValue());
        assertEquals(Integer.valueOf(1), viewModel.getCountTracked().getValue());
        assertEquals(Integer.valueOf(1), viewModel.getCountUrgent().getValue());
        assertEquals(Integer.valueOf(2), viewModel.getCountReminders().getValue());
    }

    @Test
    public void testSeedSampleDataTriggersReload() {
        when(opportunityRepository.seedSampleData("user-123")).thenReturn(Completable.complete());
        when(reminderRepository.seedSampleReminders("user-123")).thenReturn(Completable.complete());

        viewModel = new HomeViewModel(opportunityRepository, reminderRepository, "user-123");
        viewModel.seedSampleData();

        verify(opportunityRepository).seedSampleData("user-123");
        verify(reminderRepository).seedSampleReminders("user-123");
    }

    @Test
    public void testClearData() {
        when(opportunityRepository.clearDataForUser("user-123")).thenReturn(Completable.complete());
        when(reminderRepository.clearRemindersForUser("user-123")).thenReturn(Completable.complete());

        viewModel = new HomeViewModel(opportunityRepository, reminderRepository, "user-123");
        viewModel.clearData();

        verify(opportunityRepository).clearDataForUser("user-123");
        verify(reminderRepository).clearRemindersForUser("user-123");
    }
}
