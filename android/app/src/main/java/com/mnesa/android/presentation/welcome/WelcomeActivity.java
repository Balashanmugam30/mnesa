package com.mnesa.android.presentation.welcome;

import android.content.Intent;
import android.view.LayoutInflater;

import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.databinding.ActivityWelcomeBinding;
import com.mnesa.android.presentation.auth.RegisterActivity;
import com.mnesa.android.presentation.auth.SignInActivity;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    @Override
    protected ActivityWelcomeBinding inflateBinding(LayoutInflater inflater) {
        return ActivityWelcomeBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        binding.btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void observeViewModel() {
        // Static onboarding presentation
    }
}
