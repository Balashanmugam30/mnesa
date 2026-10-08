package com.mnesa.android.presentation.auth;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.repository.AuthRepositoryImpl;
import com.mnesa.android.databinding.ActivitySignInBinding;
import com.mnesa.android.presentation.main.MainActivity;

public class SignInActivity extends BaseActivity<ActivitySignInBinding> {

    private AuthViewModel viewModel;

    @Override
    protected ActivitySignInBinding inflateBinding(LayoutInflater inflater) {
        return ActivitySignInBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        ApiClient apiClient = ApiClient.getInstance(this);
        AuthRepositoryImpl authRepository = new AuthRepositoryImpl(apiClient.getAuthApiService(), apiClient.getTokenManager());
        viewModel = new AuthViewModel(authRepository);

        binding.btnSubmitSignIn.setOnClickListener(v -> {
            String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
            String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
            viewModel.login(email, password);
        });

        binding.btnGoogleSignIn.setOnClickListener(v -> {
            viewModel.googleSignIn("google-demo-token");
        });

        binding.btnForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        binding.btnGoToSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
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
                    binding.btnSubmitSignIn.setEnabled(false);
                    binding.cardError.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitSignIn.setEnabled(true);
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitSignIn.setEnabled(true);
                    binding.cardError.setVisibility(View.VISIBLE);
                    binding.tvErrorMessage.setText(state.getErrorMessage());
                    break;
                default:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitSignIn.setEnabled(true);
                    binding.cardError.setVisibility(View.GONE);
                    break;
            }
        });
    }
}
