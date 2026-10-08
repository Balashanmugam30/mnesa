package com.mnesa.android.presentation.reminders;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.domain.model.Reminder;
import com.mnesa.android.domain.repository.ReminderRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RemindersViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ReminderRepository reminderRepository;

    private RemindersViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(reminderRepository.getAllReminders(anyString()))
                .thenReturn(Flowable.just(Collections.emptyList()));
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testEmptyReminders() {
        viewModel = new RemindersViewModel(reminderRepository, "user-789");

        assertEquals(Boolean.FALSE, viewModel.getIsLoading().getValue());
        assertNotNull(viewModel.getReminders().getValue());
        assertTrue(viewModel.getReminders().getValue().isEmpty());
    }

    @Test
    public void testPopulatedReminders() {
        List<Reminder> list = new ArrayList<>();
        list.add(new Reminder("rem-1", "opp-1", "user-789", "Submit Application",
                System.currentTimeMillis() + 86400000L, "STANDARD", "SCHEDULED", System.currentTimeMillis()));

        when(reminderRepository.getAllReminders("user-789")).thenReturn(Flowable.just(list));

        viewModel = new RemindersViewModel(reminderRepository, "user-789");

        assertEquals(1, viewModel.getReminders().getValue().size());
        assertEquals("Submit Application", viewModel.getReminders().getValue().get(0).getTitle());
    }

    @Test
    public void testSeedSampleReminders() {
        when(reminderRepository.seedSampleReminders("user-789")).thenReturn(Completable.complete());

        viewModel = new RemindersViewModel(reminderRepository, "user-789");
        viewModel.seedSampleReminders();

        verify(reminderRepository).seedSampleReminders("user-789");
    }
}
