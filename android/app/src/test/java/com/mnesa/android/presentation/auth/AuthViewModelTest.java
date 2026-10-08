package com.mnesa.android.presentation.auth;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.mnesa.android.domain.model.AuthSession;
import com.mnesa.android.domain.model.User;
import com.mnesa.android.domain.repository.AuthRepository;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class AuthViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AuthRepository authRepository;

    private AuthViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        viewModel = new AuthViewModel(authRepository);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testLoginEmptyInputSetsError() {
        viewModel.login("", "");

        AuthViewModel.AuthState state = viewModel.getAuthState().getValue();
        assertNotNull(state);
        assertEquals(AuthViewModel.Status.ERROR, state.getStatus());
        assertEquals("Please enter both email and password", state.getErrorMessage());
    }

    @Test
    public void testRegisterShortPasswordSetsError() {
        viewModel.register("user@mnesa.ai", "short", "User Name");

        AuthViewModel.AuthState state = viewModel.getAuthState().getValue();
        assertNotNull(state);
        assertEquals(AuthViewModel.Status.ERROR, state.getStatus());
        assertEquals("Password must be at least 8 characters", state.getErrorMessage());
    }

    @Test
    public void testLoginSuccessTransitionsToSuccess() {
        User user = new User("u-1", "user@mnesa.ai", "User", "USER", "ACTIVE");
        AuthSession session = new AuthSession(user, "token", "refresh", 3600);
        when(authRepository.login("user@mnesa.ai", "password123")).thenReturn(Single.just(session));

        viewModel.login("user@mnesa.ai", "password123");

        AuthViewModel.AuthState state = viewModel.getAuthState().getValue();
        assertNotNull(state);
        assertEquals(AuthViewModel.Status.SUCCESS, state.getStatus());
        assertEquals("token", state.getSession().getAccessToken());
    }
}
