package com.mnesa.android.presentation.opportunities;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.mnesa.android.data.remote.dto.CreateOpportunityRequestDto;
import com.mnesa.android.data.remote.dto.UpdateOpportunityRequestDto;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.repository.OpportunityRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class EditOpportunityViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private OpportunityRepository opportunityRepository;

    private EditOpportunityViewModel viewModel;

    private Opportunity sampleOpportunity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        sampleOpportunity = new Opportunity(
                "opp-200",
                "user-1",
                "Backend Engineer Intern",
                "Amazon",
                OpportunityType.INTERNSHIP,
                "INTERNSHIP",
                OpportunityStatus.SAVED,
                "Software Dev Intern",
                "https://amazon.jobs",
                null,
                System.currentTimeMillis() + 86400000L,
                "PST",
                "College junior",
                "Seattle, WA",
                "Full-time",
                "HIGH",
                0.92f,
                1000L,
                1000L,
                "SYNCED"
        );

        viewModel = new EditOpportunityViewModel(opportunityRepository, "user-1");
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void testLoadForEditSuccess() {
        when(opportunityRepository.getOpportunityById("opp-200", "user-1"))
                .thenReturn(Single.just(sampleOpportunity));

        viewModel.loadForEdit("opp-200");

        assertNotNull(viewModel.getOpportunity().getValue());
        assertEquals("Backend Engineer Intern", viewModel.getOpportunity().getValue().getTitle());
        assertEquals("Amazon", viewModel.getOpportunity().getValue().getOrganization());
        assertEquals(Boolean.FALSE, viewModel.getIsSaving().getValue());
    }

    @Test
    public void testCreateOpportunitySuccess() {
        CreateOpportunityRequestDto req = new CreateOpportunityRequestDto(
                "Hackathon 2026", "MLH", "HACKATHON", "Hackathon event",
                "https://mlh.io", null, null, null, "All welcome", "Virtual", "REMOTE", "Weekend", "MEDIUM", null, null
        );

        when(opportunityRepository.createOpportunity(any(CreateOpportunityRequestDto.class), eq("user-1")))
                .thenReturn(Single.just(sampleOpportunity));

        viewModel.createOpportunity(req);

        assertEquals(Boolean.FALSE, viewModel.getIsSaving().getValue());
        assertEquals(Boolean.TRUE, viewModel.getSaveSuccess().getValue());
        verify(opportunityRepository).createOpportunity(eq(req), eq("user-1"));
    }

    @Test
    public void testUpdateOpportunitySuccess() {
        UpdateOpportunityRequestDto req = new UpdateOpportunityRequestDto();
        req.setTitle("Updated Title");

        when(opportunityRepository.updateOpportunity(eq("opp-200"), any(UpdateOpportunityRequestDto.class), eq("user-1")))
                .thenReturn(Single.just(sampleOpportunity));

        viewModel.updateOpportunity("opp-200", req);

        assertEquals(Boolean.FALSE, viewModel.getIsSaving().getValue());
        assertEquals(Boolean.TRUE, viewModel.getSaveSuccess().getValue());
        verify(opportunityRepository).updateOpportunity(eq("opp-200"), eq(req), eq("user-1"));
    }

    @Test
    public void testCreateOpportunityError() {
        CreateOpportunityRequestDto req = new CreateOpportunityRequestDto();
        when(opportunityRepository.createOpportunity(any(), any()))
                .thenReturn(Single.error(new RuntimeException("Network validation error")));

        viewModel.createOpportunity(req);

        assertEquals(Boolean.FALSE, viewModel.getIsSaving().getValue());
        assertNotEquals(Boolean.TRUE, viewModel.getSaveSuccess().getValue());
        assertEquals("Network validation error", viewModel.getErrorMessage().getValue());
    }
}
