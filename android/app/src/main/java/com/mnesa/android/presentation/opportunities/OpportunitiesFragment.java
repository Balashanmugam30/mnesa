package com.mnesa.android.presentation.opportunities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mnesa.android.R;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.repository.OpportunityRepositoryImpl;
import com.mnesa.android.databinding.FragmentOpportunitiesBinding;
import com.mnesa.android.presentation.common.OpportunityAdapter;

/**
 * All Opportunities tab supporting category filtering, offline viewing, skeleton loading, and empty states.
 */
public class OpportunitiesFragment extends Fragment {

    private FragmentOpportunitiesBinding binding;
    private OpportunitiesViewModel viewModel;
    private OpportunityAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOpportunitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        SecureTokenManager tokenManager = new SecureTokenManager(requireContext());
        String userId = tokenManager.getUserId();

        OpportunityRepositoryImpl oppRepo = new OpportunityRepositoryImpl(db.opportunityDao());
        viewModel = new OpportunitiesViewModel(oppRepo, userId);

        adapter = new OpportunityAdapter(opp -> {});
        binding.recyclerAllOpportunities.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAllOpportunities.setAdapter(adapter);

        setupFilterChips();

        binding.btnSeedOpportunities.setOnClickListener(v -> viewModel.seedSampleData());
        binding.btnRetryOpportunities.setOnClickListener(v ->
                viewModel.loadOpportunities(viewModel.getActiveFilter().getValue()));

        observeViewModel();
    }

    private void setupFilterChips() {
        binding.chipFilterAll.setOnClickListener(v -> viewModel.setCategoryFilter("ALL"));
        binding.chipFilterInternships.setOnClickListener(v -> viewModel.setCategoryFilter("INTERNSHIP"));
        binding.chipFilterJobs.setOnClickListener(v -> viewModel.setCategoryFilter("JOB"));
        binding.chipFilterHackathons.setOnClickListener(v -> viewModel.setCategoryFilter("HACKATHON"));
        binding.chipFilterScholarships.setOnClickListener(v -> viewModel.setCategoryFilter("SCHOLARSHIP"));
        binding.chipFilterCompetitions.setOnClickListener(v -> viewModel.setCategoryFilter("COMPETITION"));
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (Boolean.TRUE.equals(loading)) {
                binding.layoutSkeletonLoading.setVisibility(View.VISIBLE);
                binding.recyclerAllOpportunities.setVisibility(View.GONE);
                binding.layoutEmptyOpportunities.setVisibility(View.GONE);
                binding.layoutErrorOpportunities.setVisibility(View.GONE);
            } else {
                binding.layoutSkeletonLoading.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.layoutErrorOpportunities.setVisibility(View.VISIBLE);
                binding.txtErrorMessage.setText(error);
                binding.recyclerAllOpportunities.setVisibility(View.GONE);
                binding.layoutEmptyOpportunities.setVisibility(View.GONE);
            } else {
                binding.layoutErrorOpportunities.setVisibility(View.GONE);
            }
        });

        viewModel.getOpportunities().observe(getViewLifecycleOwner(), list -> {
            if (Boolean.TRUE.equals(viewModel.getIsLoading().getValue())) return;

            if (list == null || list.isEmpty()) {
                binding.layoutEmptyOpportunities.setVisibility(View.VISIBLE);
                binding.recyclerAllOpportunities.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyOpportunities.setVisibility(View.GONE);
                binding.recyclerAllOpportunities.setVisibility(View.VISIBLE);
                adapter.submitList(list);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
