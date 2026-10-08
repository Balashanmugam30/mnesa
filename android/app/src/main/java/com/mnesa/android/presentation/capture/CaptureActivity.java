package com.mnesa.android.presentation.capture;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.repository.OpportunityRepositoryImpl;
import com.mnesa.android.databinding.ActivityCaptureBinding;

/**
 * High-speed translucent Share Target activity handling ACTION_SEND and ACTION_SEND_MULTIPLE.
 */
public class CaptureActivity extends BaseActivity<ActivityCaptureBinding> {

    private CaptureViewModel viewModel;

    @Override
    protected ActivityCaptureBinding inflateBinding(LayoutInflater inflater) {
        return ActivityCaptureBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        AppDatabase database = AppDatabase.getInstance(this);
        OpportunityRepositoryImpl repository = new OpportunityRepositoryImpl(database.opportunityDao());
        viewModel = new CaptureViewModel(repository);

        binding.btnDone.setOnClickListener(v -> finish());
        binding.captureRoot.setOnClickListener(v -> finish());

        handleIncomingIntent(getIntent());
    }

    @Override
    protected void observeViewModel() {
        viewModel.getPreviewText().observe(this, text -> {
            binding.txtCapturedPreview.setText(text);
        });

        viewModel.getCaptureCompleted().observe(this, completed -> {
            if (completed != null && completed) {
                // Instantly confirm capture to the user
                binding.txtCaptureStatus.setText(com.mnesa.android.R.string.capture_success);
            }
        });
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) {
            finish();
            return;
        }

        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type) || type.startsWith("text/")) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                viewModel.processSharedContent(sharedText, sharedText);
            } else if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                String uriString = imageUri != null ? imageUri.toString() : "Shared Image";
                viewModel.processSharedContent(uriString, null);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            viewModel.processSharedContent("Multiple items shared", null);
        } else {
            finish();
        }
    }
}
