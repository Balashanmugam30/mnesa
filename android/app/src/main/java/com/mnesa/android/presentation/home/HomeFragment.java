package com.mnesa.android.presentation.home;

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
import com.mnesa.android.data.repository.ReminderRepositoryImpl;
import com.mnesa.android.databinding.FragmentHomeBinding;
import com.mnesa.android.presentation.common.OpportunityAdapter;

import java.util.Calendar;

/**
 * Home tab displaying the opportunity dashboard, progress metrics, and urgency buckets.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private OpportunityAdapter adapterNeedsAttention;
    private OpportunityAdapter adapterUpcoming;
    private OpportunityAdapter adapterRecentlySaved;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Dynamic greeting
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            binding.txtGreeting.setText(R.string.greeting_morning);
        } else if (hour < 17) {
            binding.txtGreeting.setText(R.string.greeting_afternoon);
        } else {
            binding.txtGreeting.setText(R.string.greeting_evening);
        }

        // Initialize dependencies & ViewModel
        AppDatabase db = AppDatabase.getInstance(requireContext());
        SecureTokenManager tokenManager = new SecureTokenManager(requireContext());
        String userId = tokenManager.getUserId();

        OpportunityRepositoryImpl oppRepo = new OpportunityRepositoryImpl(db.opportunityDao());
        ReminderRepositoryImpl reminderRepo = new ReminderRepositoryImpl(db.reminderDao());
        viewModel = new HomeViewModel(oppRepo, reminderRepo, userId);

        // Setup RecyclerViews
        adapterNeedsAttention = new OpportunityAdapter(opp -> {});
        binding.recyclerNeedsAttention.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerNeedsAttention.setAdapter(adapterNeedsAttention);

        adapterUpcoming = new OpportunityAdapter(opp -> {});
        binding.recyclerUpcoming.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUpcoming.setAdapter(adapterUpcoming);

        adapterRecentlySaved = new OpportunityAdapter(opp -> {});
        binding.recyclerRecentlySaved.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRecentlySaved.setAdapter(adapterRecentlySaved);

        // Buttons
        binding.btnSeedSampleHome.setOnClickListener(v -> viewModel.seedSampleData());
        binding.btnQuickLoadSample.setOnClickListener(v -> viewModel.seedSampleData());
        binding.btnClearData.setOnClickListener(v -> viewModel.clearData());

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getCountTracked().observe(getViewLifecycleOwner(), count ->
                binding.txtCountTracked.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getCountUrgent().observe(getViewLifecycleOwner(), count ->
                binding.txtCountUrgent.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getCountReminders().observe(getViewLifecycleOwner(), count ->
                binding.txtCountReminders.setText(String.valueOf(count != null ? count : 0)));

        viewModel.getIsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (Boolean.TRUE.equals(isEmpty)) {
                binding.layoutEmptyHome.setVisibility(View.VISIBLE);
                binding.sectionNeedsAttention.setVisibility(View.GONE);
                binding.sectionUpcoming.setVisibility(View.GONE);
                binding.sectionRecentlySaved.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyHome.setVisibility(View.GONE);
            }
        });

        viewModel.getNeedsAttention().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.sectionNeedsAttention.setVisibility(View.GONE);
            } else {
                binding.sectionNeedsAttention.setVisibility(View.VISIBLE);
                adapterNeedsAttention.submitList(list);
            }
        });

        viewModel.getUpcoming().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.sectionUpcoming.setVisibility(View.GONE);
            } else {
                binding.sectionUpcoming.setVisibility(View.VISIBLE);
                adapterUpcoming.submitList(list);
            }
        });

        viewModel.getRecentlySaved().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.sectionRecentlySaved.setVisibility(View.GONE);
            } else {
                binding.sectionRecentlySaved.setVisibility(View.VISIBLE);
                adapterRecentlySaved.submitList(list);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
