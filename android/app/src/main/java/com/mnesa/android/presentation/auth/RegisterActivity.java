package com.mnesa.android.presentation.auth;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.repository.AuthRepositoryImpl;
import com.mnesa.android.databinding.ActivityRegisterBinding;
import com.mnesa.android.presentation.onboarding.OnboardingInterestsActivity;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {

    private AuthViewModel viewModel;

    @Override
    protected ActivityRegisterBinding inflateBinding(LayoutInflater inflater) {
        return ActivityRegisterBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        ApiClient apiClient = ApiClient.getInstance(this);
        AuthRepositoryImpl authRepository = new AuthRepositoryImpl(apiClient.getAuthApiService(), apiClient.getTokenManager());
        viewModel = new AuthViewModel(authRepository);

        binding.btnSubmitSignUp.setOnClickListener(v -> {
            String name = binding.etFullName.getText() != null ? binding.etFullName.getText().toString() : "";
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
            viewModel.register(email, password, name);
        });

        binding.btnGoToSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void observeViewModel() {
        viewModel.getAuthState().observe(this, state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnSubmitSignUp.setEnabled(false);
                    binding.cardError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitSignUp.setEnabled(true);
                    Intent intent = new Intent(RegisterActivity.this, OnboardingInterestsActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitSignUp.setEnabled(true);
                    binding.cardError.setVisibility(View.VISIBLE);
                    binding.tvErrorMessage.setText(state.getErrorMessage());
                    break;
                default:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitSignUp.setEnabled(true);
                    binding.cardError.setVisibility(View.GONE);
                    break;
            }
        });
    }
}
