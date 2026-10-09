package com.mnesa.android.presentation.capture;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.mnesa.android.R;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.data.repository.IntakeRepositoryImpl;
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
        IntakeRepositoryImpl repository = new IntakeRepositoryImpl(this);
        viewModel = new CaptureViewModel(repository);

        binding.btnDone.setOnClickListener(v -> finish());
        binding.btnRetry.setOnClickListener(v -> viewModel.retry());
        binding.btnConfirmOpportunity.setOnClickListener(v -> viewModel.confirmOpportunity(null, null, null));
        binding.captureRoot.setOnClickListener(v -> finish());

        handleIncomingIntent(getIntent());
    }

    @Override
    protected void observeViewModel() {
        viewModel.getUiState().observe(this, state -> {
            if (state == null) return;

            binding.txtCaptureTitle.setText(state.getSubtitle());
            binding.txtCapturedPreview.setText(state.getTitle());
            binding.badgeSourceType.setText(state.getSourceBadge());
            binding.txtCaptureStatus.setText(state.getStatusMessage());

            if (state.isExtractionReady()) {
                binding.cardExtraction.setVisibility(View.VISIBLE);
                String org = state.getOrganization();
                binding.txtExtractedOrg.setText(org != null && !org.isBlank() ? org : state.getCategory());
                binding.badgeConfidence.setText(state.getConfidencePill());

                if (state.getConfidenceScore() >= 0.85f) {
                    binding.badgeConfidence.setTextColor(ContextCompat.getColor(this, R.color.mnesa_status_success));
                } else if (state.getConfidenceScore() >= 0.50f) {
                    binding.badgeConfidence.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_warning));
                } else {
                    binding.badgeConfidence.setTextColor(ContextCompat.getColor(this, R.color.mnesa_error));
                }

                if (state.getDeadlineFormatted() != null) {
                    binding.txtExtractedDeadline.setVisibility(View.VISIBLE);
                    binding.txtExtractedDeadline.setText(getString(R.string.capture_deadline_label, state.getDeadlineFormatted()));
                } else {
                    binding.txtExtractedDeadline.setVisibility(View.GONE);
                }

                if (state.getSummary() != null && !state.getSummary().isBlank()) {
                    binding.txtExtractedSummary.setVisibility(View.VISIBLE);
                    binding.txtExtractedSummary.setText(state.getSummary());
                } else {
                    binding.txtExtractedSummary.setVisibility(View.GONE);
                }

                if (state.getEvidenceSnippet() != null && !state.getEvidenceSnippet().isBlank()) {
                    binding.txtEvidenceSnippet.setVisibility(View.VISIBLE);
                    binding.txtEvidenceSnippet.setText(getString(R.string.capture_evidence_label, state.getEvidenceSnippet()));
                } else {
                    binding.txtEvidenceSnippet.setVisibility(View.GONE);
                }
            } else {
                binding.cardExtraction.setVisibility(View.GONE);
            }

            if (state.isProgressVisible()) {
                binding.progressSpinner.setVisibility(View.VISIBLE);
                binding.imgCaptureIcon.setVisibility(View.GONE);
            } else {
                binding.progressSpinner.setVisibility(View.GONE);
                binding.imgCaptureIcon.setVisibility(View.VISIBLE);

                if (state.isOffline()) {
                    binding.imgCaptureIcon.setImageResource(R.drawable.ic_wifi_off);
                    binding.imgCaptureIcon.setColorFilter(ContextCompat.getColor(this, R.color.mnesa_urgency_warning));
                    binding.txtCaptureStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_warning));
                } else if (state.isDuplicate()) {
                    binding.imgCaptureIcon.setImageResource(R.drawable.ic_status_alert);
                    binding.imgCaptureIcon.setColorFilter(ContextCompat.getColor(this, R.color.mnesa_urgency_warning));
                    binding.txtCaptureStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_warning));
                } else if (state.isError()) {
                    binding.imgCaptureIcon.setImageResource(R.drawable.ic_status_alert);
                    binding.imgCaptureIcon.setColorFilter(ContextCompat.getColor(this, R.color.mnesa_error));
                    binding.txtCaptureStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_error));
                } else {
                    binding.imgCaptureIcon.setImageResource(R.drawable.ic_status_check);
                    binding.imgCaptureIcon.setColorFilter(ContextCompat.getColor(this, R.color.mnesa_status_success));
                    binding.txtCaptureStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_status_success));
                }
            }

            binding.btnRetry.setVisibility(state.canRetry() ? View.VISIBLE : View.GONE);
            binding.btnConfirmOpportunity.setVisibility(state.canConfirm() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) {
            finish();
            return;
        }
        viewModel.processIncomingIntent(intent, getContentResolver());
    }
}
