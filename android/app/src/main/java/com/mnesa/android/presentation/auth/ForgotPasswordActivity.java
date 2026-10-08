package com.mnesa.android.presentation.auth;

import android.view.LayoutInflater;
import android.view.View;

import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.repository.AuthRepositoryImpl;
import com.mnesa.android.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends BaseActivity<ActivityForgotPasswordBinding> {

    private AuthViewModel viewModel;

    @Override
    protected ActivityForgotPasswordBinding inflateBinding(LayoutInflater inflater) {
        return ActivityForgotPasswordBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        ApiClient apiClient = ApiClient.getInstance(this);
        AuthRepositoryImpl authRepository = new AuthRepositoryImpl(apiClient.getAuthApiService(), apiClient.getTokenManager());
        viewModel = new AuthViewModel(authRepository);

        binding.btnSubmitForgot.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
            viewModel.forgotPassword(email);
        });

        binding.btnBackToSignIn.setOnClickListener(v -> finish());
    }

    @Override
    protected void observeViewModel() {
        viewModel.getAuthState().observe(this, state -> {
            if (state == null) return;

            switch (state.getStatus()) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnSubmitForgot.setEnabled(false);
                    binding.cardSuccess.setVisibility(View.GONE);
                    break;
                case FORGOT_PASSWORD_SENT:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitForgot.setEnabled(true);
                    binding.cardSuccess.setVisibility(View.VISIBLE);
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitForgot.setEnabled(true);
                    binding.cardSuccess.setVisibility(View.GONE);
                    break;
                default:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitForgot.setEnabled(true);
                    break;
            }
        });
    }
}
