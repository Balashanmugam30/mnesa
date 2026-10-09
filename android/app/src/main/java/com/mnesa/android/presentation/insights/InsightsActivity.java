package com.mnesa.android.presentation.insights;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.android.material.chip.Chip;
import com.mnesa.android.R;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.data.remote.dto.ActivityTrendPointDto;
import com.mnesa.android.data.repository.InsightsRepositoryImpl;
import com.mnesa.android.databinding.ActivityInsightsBinding;

import java.util.List;
import java.util.Map;

public class InsightsActivity extends BaseActivity<ActivityInsightsBinding> {

    private InsightsViewModel viewModel;

    public static void start(Context context) {
        Intent intent = new Intent(context, InsightsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected ActivityInsightsBinding inflateBinding(LayoutInflater inflater) {
        return ActivityInsightsBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        InsightsRepositoryImpl repository = new InsightsRepositoryImpl(this);
        viewModel = new InsightsViewModel(repository);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnRetry.setOnClickListener(v -> viewModel.loadInsights());
    }

    @Override
    protected void observeViewModel() {
        viewModel.getUiState().observe(this, state -> {
            if (state == null) return;

            if (state.isLoading()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.scrollContent.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.GONE);
                return;
            }

            if (state.isError()) {
                binding.progressBar.setVisibility(View.GONE);
                binding.scrollContent.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.VISIBLE);
                binding.txtErrorMessage.setText(
                        state.getErrorMessage() != null ? state.getErrorMessage() : "Failed to load insights"
                );
                return;
            }

            binding.progressBar.setVisibility(View.GONE);
            binding.layoutError.setVisibility(View.GONE);
            binding.scrollContent.setVisibility(View.VISIBLE);

            // Populate core metrics
            binding.txtTotalSaved.setText(String.valueOf(state.getTotalSaved()));
            binding.txtCompleted.setText(String.valueOf(state.getApplicationsCompleted()));
            binding.txtUpcoming.setText(String.valueOf(state.getUpcomingDeadlines()));
            binding.txtMissed.setText(String.valueOf(state.getMissedOpportunities()));

            // Populate categories
            binding.chipGroupCategories.removeAllViews();
            Map<String, Long> catDist = state.getCategoryDistribution();
            if (catDist != null && !catDist.isEmpty()) {
                for (Map.Entry<String, Long> entry : catDist.entrySet()) {
                    Chip chip = new Chip(this);
                    chip.setText(entry.getKey() + ": " + entry.getValue());
                    chip.setChipBackgroundColorResource(R.color.mnesa_surface);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.mnesa_text_primary));
                    chip.setChipStrokeColorResource(R.color.mnesa_border_subtle);
                    chip.setChipStrokeWidth(1.0f);
                    binding.chipGroupCategories.addView(chip);
                }
            } else {
                Chip emptyChip = new Chip(this);
                emptyChip.setText("No categories tracked yet");
                binding.chipGroupCategories.addView(emptyChip);
            }

            // Populate statuses
            binding.chipGroupStatuses.removeAllViews();
            Map<String, Long> statusDist = state.getStatusDistribution();
            if (statusDist != null && !statusDist.isEmpty()) {
                for (Map.Entry<String, Long> entry : statusDist.entrySet()) {
                    Chip chip = new Chip(this);
                    chip.setText(entry.getKey() + ": " + entry.getValue());
                    chip.setChipBackgroundColorResource(R.color.mnesa_surface);
                    chip.setTextColor(ContextCompat.getColor(this, R.color.mnesa_text_secondary));
                    chip.setChipStrokeColorResource(R.color.mnesa_border_subtle);
                    chip.setChipStrokeWidth(1.0f);
                    binding.chipGroupStatuses.addView(chip);
                }
            }

            // Populate activity trends
            binding.layoutTrendsContainer.removeAllViews();
            List<ActivityTrendPointDto> trends = state.getActivityTrends();
            if (trends != null && !trends.isEmpty()) {
                for (ActivityTrendPointDto point : trends) {
                    LinearLayout row = new LinearLayout(this);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setPadding(0, 8, 0, 8);

                    TextView dateTxt = new TextView(this);
                    dateTxt.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
                    dateTxt.setText(point.getDate());
                    dateTxt.setTextColor(ContextCompat.getColor(this, R.color.mnesa_text_secondary));
                    dateTxt.setTextSize(13f);

                    TextView countTxt = new TextView(this);
                    countTxt.setText(point.getCount() + " actions");
                    countTxt.setTextColor(ContextCompat.getColor(this, R.color.mnesa_primary));
                    countTxt.setTextSize(13f);

                    row.addView(dateTxt);
                    row.addView(countTxt);
                    binding.layoutTrendsContainer.addView(row);
                }
            } else {
                TextView noData = new TextView(this);
                noData.setText("No activity recorded in the past 14 days");
                noData.setTextColor(ContextCompat.getColor(this, R.color.mnesa_text_muted));
                noData.setTextSize(13f);
                binding.layoutTrendsContainer.addView(noData);
            }
        });
    }
}
