package com.mnesa.android.presentation.settings;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mnesa.android.R;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.repository.AuthRepositoryImpl;
import com.mnesa.android.data.repository.UserRepositoryImpl;
import com.mnesa.android.databinding.ActivitySettingsBinding;
import com.mnesa.android.domain.model.UserPreferences;
import com.mnesa.android.presentation.welcome.WelcomeActivity;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity<ActivitySettingsBinding> {

    private SettingsViewModel viewModel;

    @Override
    protected ActivitySettingsBinding inflateBinding(LayoutInflater inflater) {
        return ActivitySettingsBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        ApiClient apiClient = ApiClient.getInstance(this);
        AuthRepositoryImpl authRepository = new AuthRepositoryImpl(apiClient.getAuthApiService(), apiClient.getTokenManager());
        UserRepositoryImpl userRepository = new UserRepositoryImpl(apiClient.getUserApiService(), apiClient.getTokenManager());
        viewModel = new SettingsViewModel(userRepository, authRepository);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnSignOut.setOnClickListener(v -> viewModel.logout());

        binding.btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());

        binding.switchPush.setOnCheckedChangeListener((btn, isChecked) -> saveNotificationPreferences());
        binding.switchEmail.setOnCheckedChangeListener((btn, isChecked) -> saveNotificationPreferences());

        viewModel.loadProfileAndPreferences();
    }

    private void saveNotificationPreferences() {
        UserPreferences current = viewModel.getUserPreferences().getValue();
        UserPreferences updated = new UserPreferences(
                current != null ? current.getInterests() : new ArrayList<>(),
                current != null ? current.getReminderTiming() : "STANDARD",
                binding.switchEmail.isChecked(),
                binding.switchPush.isChecked()
        );
        viewModel.updatePreferences(updated);
    }

    private void showDeleteAccountDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_delete_account_title)
                .setMessage(R.string.dialog_delete_account_msg)
                .setPositiveButton(R.string.action_delete_account, (dialog, which) -> viewModel.deleteAccount())
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }

    @Override
    protected void observeViewModel() {
        viewModel.getLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(Boolean.TRUE.equals(isLoading) ? View.VISIBLE : View.GONE);
        });

        viewModel.getUserProfile().observe(this, user -> {
            if (user != null) {
                binding.tvUserName.setText(user.getFullName() != null && !user.getFullName().isEmpty()
                        ? user.getFullName() : "MNESA Member");
                binding.tvUserEmail.setText(user.getEmail());
                binding.tvAccountStatus.setText(user.getStatus());
            }
        });

        viewModel.getUserPreferences().observe(this, prefs -> {
            if (prefs != null) {
                binding.switchPush.setChecked(prefs.isPushNotificationsEnabled());
                binding.switchEmail.setChecked(prefs.isEmailNotificationsEnabled());
            }
        });

        viewModel.getLoggedOut().observe(this, isLoggedOut -> {
            if (Boolean.TRUE.equals(isLoggedOut)) {
                navigateToWelcome();
            }
        });

        viewModel.getAccountDeleted().observe(this, isDeleted -> {
            if (Boolean.TRUE.equals(isDeleted)) {
                Toast.makeText(this, "Your account has been deleted.", Toast.LENGTH_LONG).show();
                navigateToWelcome();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToWelcome() {
        Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
