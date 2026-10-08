package com.mnesa.android.presentation.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;

import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.databinding.ActivitySplashBinding;
import com.mnesa.android.presentation.main.MainActivity;
import com.mnesa.android.presentation.welcome.WelcomeActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {

    private SecureTokenManager tokenManager;

    @Override
    protected ActivitySplashBinding inflateBinding(LayoutInflater inflater) {
        return ActivitySplashBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        tokenManager = new SecureTokenManager(this);

        // Brief delay for brand perception
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, 600);
    }

    @Override
    protected void observeViewModel() {
        // No ViewModel required for simple splash router
    }

    private void navigateNext() {
        Intent targetIntent;
        if (tokenManager.isLoggedIn()) {
            targetIntent = new Intent(this, MainActivity.class);
        } else {
            targetIntent = new Intent(this, WelcomeActivity.class);
        }
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(targetIntent);
        finish();
    }
}
