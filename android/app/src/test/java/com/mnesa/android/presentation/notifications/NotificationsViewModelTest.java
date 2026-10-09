package com.mnesa.android.presentation.notifications;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.domain.model.NotificationItem;
import com.mnesa.android.domain.repository.NotificationRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NotificationsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationsViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        when(notificationRepository.getNotifications(anyString()))
                .thenReturn(Flowable.just(Collections.emptyList()));
        when(notificationRepository.getUnreadCount(anyString()))
                .thenReturn(Single.just(0));
        when(notificationRepository.syncNotifications(anyString()))
                .thenReturn(Completable.complete());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testEmptyNotifications() {
        viewModel = new NotificationsViewModel(notificationRepository, "user-456");

        assertEquals(Boolean.FALSE, viewModel.getIsLoading().getValue());
        assertNotNull(viewModel.getNotifications().getValue());
        assertTrue(viewModel.getNotifications().getValue().isEmpty());
        assertEquals(Integer.valueOf(0), viewModel.getUnreadCount().getValue());
    }

    @Test
    public void testPopulatedNotifications() {
        List<NotificationItem> list = new ArrayList<>();
        list.add(new NotificationItem("notif-1", "user-456", "rem-1", "opp-1",
                "Deadline approaching", "Only 24 hours remaining.", "PUSH", "FCM",
                "DELIVERED", "mnesa://opportunity/opp-1", null, System.currentTimeMillis()));

        when(notificationRepository.getNotifications("user-456")).thenReturn(Flowable.just(list));
        when(notificationRepository.getUnreadCount("user-456")).thenReturn(Single.just(1));

        viewModel = new NotificationsViewModel(notificationRepository, "user-456");

        assertEquals(1, viewModel.getNotifications().getValue().size());
        assertEquals("Deadline approaching", viewModel.getNotifications().getValue().get(0).getTitle());
        assertEquals(Integer.valueOf(1), viewModel.getUnreadCount().getValue());
    }

    @Test
    public void testMarkOpened() {
        when(notificationRepository.markOpened("notif-1", "user-456")).thenReturn(Completable.complete());

        viewModel = new NotificationsViewModel(notificationRepository, "user-456");
        viewModel.markOpened("notif-1");

        verify(notificationRepository).markOpened("notif-1", "user-456");
    }

    @Test
    public void testMarkAllOpened() {
        when(notificationRepository.markAllOpened("user-456")).thenReturn(Completable.complete());

        viewModel = new NotificationsViewModel(notificationRepository, "user-456");
        viewModel.markAllOpened();

        verify(notificationRepository).markAllOpened("user-456");
    }

    @Test
    public void testSync() {
        when(notificationRepository.syncNotifications("user-456")).thenReturn(Completable.complete());

        viewModel = new NotificationsViewModel(notificationRepository, "user-456");
        viewModel.sync();

        verify(notificationRepository, atLeastOnce()).syncNotifications("user-456");
    }
}
