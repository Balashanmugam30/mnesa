package com.mnesa.android.presentation.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.repository.SyncRepositoryImpl;
import com.mnesa.android.databinding.FragmentProfileBinding;
import com.mnesa.android.presentation.settings.SettingsActivity;
import com.mnesa.android.presentation.welcome.WelcomeActivity;

/**
 * Profile tab displaying user identity, preferences, storage architecture, and navigation to Settings.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SecureTokenManager tokenManager = new SecureTokenManager(requireContext());
        AppDatabase db = AppDatabase.getInstance(requireContext());
        SyncRepositoryImpl syncRepo = new SyncRepositoryImpl(db.syncQueueDao());

        viewModel = new ProfileViewModel(tokenManager, syncRepo);

        binding.btnOpenSettings.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SettingsActivity.class)));

        binding.btnSignOut.setOnClickListener(v -> viewModel.signOut());

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getName().observe(getViewLifecycleOwner(), name ->
                binding.txtProfileName.setText(name));

        viewModel.getEmail().observe(getViewLifecycleOwner(), email ->
                binding.txtProfileEmail.setText(email));

        viewModel.getInitials().observe(getViewLifecycleOwner(), initials ->
                binding.txtAvatarInitials.setText(initials));

        viewModel.getSignedOut().observe(getViewLifecycleOwner(), signedOut -> {
            if (Boolean.TRUE.equals(signedOut)) {
                Intent intent = new Intent(requireContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
