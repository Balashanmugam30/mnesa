package com.mnesa.android.presentation.reminders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.repository.ReminderRepositoryImpl;
import com.mnesa.android.databinding.FragmentRemindersBinding;
import com.mnesa.android.presentation.common.ReminderAdapter;

/**
 * Reminders tab presenting scheduled reminder cadences and deadlines.
 */
public class RemindersFragment extends Fragment {

    private FragmentRemindersBinding binding;
    private RemindersViewModel viewModel;
    private ReminderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRemindersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        SecureTokenManager tokenManager = new SecureTokenManager(requireContext());
        String userId = tokenManager.getUserId();

        ReminderRepositoryImpl reminderRepo = new ReminderRepositoryImpl(db.reminderDao());
        viewModel = new RemindersViewModel(reminderRepo, userId);

        adapter = new ReminderAdapter(rem -> {});
        binding.recyclerReminders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerReminders.setAdapter(adapter);

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (Boolean.TRUE.equals(loading)) {
                binding.layoutSkeletonReminders.setVisibility(View.VISIBLE);
                binding.recyclerReminders.setVisibility(View.GONE);
                binding.layoutEmptyReminders.setVisibility(View.GONE);
            } else {
                binding.layoutSkeletonReminders.setVisibility(View.GONE);
            }
        });

        viewModel.getReminders().observe(getViewLifecycleOwner(), list -> {
            if (Boolean.TRUE.equals(viewModel.getIsLoading().getValue())) return;

            if (list == null || list.isEmpty()) {
                binding.layoutEmptyReminders.setVisibility(View.VISIBLE);
                binding.recyclerReminders.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyReminders.setVisibility(View.GONE);
                binding.recyclerReminders.setVisibility(View.VISIBLE);
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
