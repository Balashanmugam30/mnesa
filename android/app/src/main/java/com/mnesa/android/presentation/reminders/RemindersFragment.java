package com.mnesa.android.presentation.reminders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mnesa.android.R;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.repository.ReminderRepositoryImpl;
import com.mnesa.android.databinding.FragmentRemindersBinding;
import com.mnesa.android.domain.model.Reminder;
import com.mnesa.android.presentation.common.ReminderAdapter;
import com.mnesa.android.presentation.notifications.NotificationsActivity;
import com.mnesa.android.presentation.opportunities.OpportunityDetailActivity;

/**
 * Reminders tab presenting filterable reminder cadences, snooze controls, and creation actions.
 */
public class RemindersFragment extends Fragment implements ReminderAdapter.OnReminderActionListener {

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

        ApiClient apiClient = ApiClient.getInstance(requireContext());
        ReminderRepositoryImpl reminderRepo = new ReminderRepositoryImpl(db.reminderDao(), apiClient.getReminderApiService());
        viewModel = new RemindersViewModel(reminderRepo, userId);

        adapter = new ReminderAdapter(this);
        binding.recyclerReminders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerReminders.setAdapter(adapter);

        setupFilters();
        setupClickListeners();
        observeViewModel();
    }

    private void setupFilters() {
        binding.chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipUpcoming) {
                viewModel.setFilter("upcoming");
            } else if (checkedId == R.id.chipNeedsAttention) {
                viewModel.setFilter("needs_attention");
            } else if (checkedId == R.id.chipSnoozed) {
                viewModel.setFilter("snoozed");
            } else if (checkedId == R.id.chipHistory) {
                viewModel.setFilter("history");
            } else if (checkedId == R.id.chipAll) {
                viewModel.setFilter("all");
            }
        });
    }

    private void setupClickListeners() {
        binding.btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NotificationsActivity.class);
            startActivity(intent);
        });

        binding.fabAddReminder.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ReminderEditorActivity.class);
            startActivity(intent);
        });
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
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadReminders();
        }
    }

    @Override
    public void onReminderClick(Reminder reminder) {
        if (reminder.getOpportunityId() != null && !reminder.getOpportunityId().trim().isEmpty()) {
            Intent intent = new Intent(requireContext(), OpportunityDetailActivity.class);
            intent.putExtra(OpportunityDetailActivity.EXTRA_OPPORTUNITY_ID, reminder.getOpportunityId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(requireContext(), ReminderEditorActivity.class);
            intent.putExtra(ReminderEditorActivity.EXTRA_REMINDER_ID, reminder.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onSnoozeClick(Reminder reminder) {
        String[] options = new String[]{"1 hour", "3 hours", "Tomorrow morning (24 hours)"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Snooze Reminder")
                .setItems(options, (dialog, which) -> {
                    int minutes = 60;
                    if (which == 1) minutes = 180;
                    else if (which == 2) minutes = 1440;
                    viewModel.snoozeReminder(reminder.getId(), minutes);
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onDismissClick(Reminder reminder) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Dismiss Reminder")
                .setMessage("Mark this reminder as dismissed?")
                .setPositiveButton("Dismiss", (dialog, which) -> viewModel.dismissReminder(reminder.getId()))
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
