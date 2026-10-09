package com.mnesa.android.presentation.assistant;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.data.remote.dto.AssistantQueryResponseDto;
import com.mnesa.android.data.remote.dto.CitedOpportunityDto;
import com.mnesa.android.domain.repository.AssistantRepository;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AssistantViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AssistantRepository assistantRepository;

    private AssistantViewModel viewModel;

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
    public void testInitialStateHasWelcomeMessage() {
        viewModel = new AssistantViewModel(assistantRepository);

        AssistantUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertFalse(state.isLoading());
        assertFalse(state.getMessages().isEmpty());
        assertFalse(state.getPromptSuggestions().isEmpty());
    }

    @Test
    public void testSendQuerySuccess() {
        AssistantQueryResponseDto response = new AssistantQueryResponseDto();
        response.setQuestion("What deadlines do I have?");
        response.setAnswer("You have 1 deadline coming up on Nov 15.");
        response.setIntent("QUERY_DEADLINES");
        CitedOpportunityDto cited = new CitedOpportunityDto();
        cited.setId("opp-123");
        cited.setTitle("Google Fellowship");
        response.setCitedOpportunities(Collections.singletonList(cited));
        response.setActionSuggestions(Collections.singletonList("Set a reminder"));

        when(assistantRepository.askAssistant(anyString(), anyString()))
                .thenReturn(Single.just(response));

        viewModel = new AssistantViewModel(assistantRepository);
        viewModel.sendQuery("What deadlines do I have?");

        verify(assistantRepository).askAssistant(anyString(), anyString());

        AssistantUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertFalse(state.isLoading());
        // Initial welcome message + user message + assistant reply = 3 messages
        assertEquals(3, state.getMessages().size());
        assertEquals("You have 1 deadline coming up on Nov 15.", state.getMessages().get(2).getText());
        assertEquals(1, state.getMessages().get(2).getCitations().size());
        assertEquals("opp-123", state.getMessages().get(2).getCitations().get(0).getId());
    }

    @Test
    public void testSendQueryError() {
        when(assistantRepository.askAssistant(anyString(), anyString()))
                .thenReturn(Single.error(new RuntimeException("Network timeout")));

        viewModel = new AssistantViewModel(assistantRepository);
        viewModel.sendQuery("Will this fail?");

        AssistantUiState state = viewModel.getUiState().getValue();
        assertNotNull(state);
        assertFalse(state.isLoading());
        assertEquals("Network timeout", state.getErrorMessage());
    }
}
