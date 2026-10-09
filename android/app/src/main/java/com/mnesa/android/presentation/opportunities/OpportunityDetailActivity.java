package com.mnesa.android.presentation.opportunities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mnesa.android.R;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.core.utils.DateTimeUtils;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.remote.dto.OpportunityActivityDto;
import com.mnesa.android.data.repository.OpportunityRepositoryImpl;
import com.mnesa.android.databinding.ActivityOpportunityDetailBinding;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for viewing complete opportunity metadata, history audit trail,
 * and performing lifecycle actions (status update, edit, archive, restore).
 */
public class OpportunityDetailActivity extends BaseActivity<ActivityOpportunityDetailBinding> {

    public static final String EXTRA_OPPORTUNITY_ID = "extra_opportunity_id";

    private OpportunityDetailViewModel viewModel;
    private String opportunityId;

    public static void start(Context context, String opportunityId) {
        Intent intent = new Intent(context, OpportunityDetailActivity.class);
        intent.putExtra(EXTRA_OPPORTUNITY_ID, opportunityId);
        context.startActivity(intent);
    }

    @Override
    protected ActivityOpportunityDetailBinding inflateBinding(LayoutInflater inflater) {
        return ActivityOpportunityDetailBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        opportunityId = getIntent().getStringExtra(EXTRA_OPPORTUNITY_ID);
        if (opportunityId == null || opportunityId.isEmpty()) {
            Toast.makeText(this, "Opportunity ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        AppDatabase db = AppDatabase.getInstance(this);
        SecureTokenManager tokenManager = new SecureTokenManager(this);
        String userId = tokenManager.getUserId();
        OpportunityRepositoryImpl oppRepo = new OpportunityRepositoryImpl(this);

        viewModel = new OpportunityDetailViewModel(oppRepo, userId);

        binding.btnEditOpportunity.setOnClickListener(v -> {
            EditOpportunityActivity.start(this, opportunityId);
        });

        binding.btnChangeStatus.setOnClickListener(v -> showStatusSelectionDialog());

        viewModel.loadOpportunity(opportunityId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null && opportunityId != null) {
            viewModel.loadOpportunity(opportunityId);
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel.getIsLoading().observe(this, loading -> {
            binding.progressBar.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getActionSuccess().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getOpportunity().observe(this, this::bindOpportunityData);
        viewModel.getHistory().observe(this, this::bindHistoryData);
    }

    private void bindOpportunityData(Opportunity opp) {
        if (opp == null) return;

        binding.txtTitle.setText(opp.getTitle());
        binding.txtOrganization.setText(opp.getOrganization());
        binding.badgeCategory.setText(opp.getCategory() != null ? opp.getCategory().toUpperCase() : "OPPORTUNITY");

        // Status badge styling
        if (opp.getStatus() != null) {
            binding.badgeStatus.setText(opp.getStatus().name());
            switch (opp.getStatus()) {
                case SELECTED:
                case COMPLETED:
                    binding.badgeStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_status_success));
                    break;
                case REJECTED:
                case MISSED:
                    binding.badgeStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_critical));
                    break;
                case APPLYING:
                case REVIEWING:
                    binding.badgeStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_primary));
                    break;
                case APPLIED:
                case WAITING:
                    binding.badgeStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_warning));
                    break;
                default:
                    binding.badgeStatus.setTextColor(ContextCompat.getColor(this, R.color.mnesa_text_secondary));
                    break;
            }
        }

        // Priority badge
        if (opp.getPriority() != null) {
            binding.badgePriority.setText(opp.getPriority().toUpperCase());
        }

        // Deadline
        if (opp.hasDeadline()) {
            binding.cardDeadline.setVisibility(View.VISIBLE);
            long diffDays = (opp.getDeadlineTimestamp() - System.currentTimeMillis()) / (1000L * 60 * 60 * 24);
            if (diffDays <= 0) {
                binding.txtDeadlineCountdown.setText("Deadline is today");
                binding.txtDeadlineCountdown.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_critical));
            } else if (diffDays <= 3) {
                binding.txtDeadlineCountdown.setText(diffDays + (diffDays == 1 ? " day remaining" : " days remaining"));
                binding.txtDeadlineCountdown.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_critical));
            } else {
                binding.txtDeadlineCountdown.setText(DateTimeUtils.formatRelativeDeadline(opp.getDeadlineTimestamp()));
                binding.txtDeadlineCountdown.setTextColor(ContextCompat.getColor(this, R.color.mnesa_urgency_warning));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault());
            String formattedDate = sdf.format(new Date(opp.getDeadlineTimestamp()));
            if (opp.getDeadlineTimezone() != null && !opp.getDeadlineTimezone().isEmpty()) {
                formattedDate += " (" + opp.getDeadlineTimezone() + ")";
            }
            binding.txtDeadlineDate.setText(formattedDate);
        } else {
            binding.cardDeadline.setVisibility(View.GONE);
        }

        // Details
        binding.txtLocation.setText("Location: " + (opp.getLocation() != null && !opp.getLocation().isEmpty() ? opp.getLocation() : "Unspecified"));
        binding.txtEligibility.setText("Eligibility: " + (opp.getEligibility() != null && !opp.getEligibility().isEmpty() ? opp.getEligibility() : "Open to all"));
        binding.txtEstimatedEffort.setText("Effort: " + (opp.getEstimatedEffort() != null && !opp.getEstimatedEffort().isEmpty() ? opp.getEstimatedEffort() : "Not specified"));

        if (opp.getConfidenceScore() > 0) {
            binding.txtConfidenceScore.setVisibility(View.VISIBLE);
            int pct = (int) (opp.getConfidenceScore() * 100);
            binding.txtConfidenceScore.setText("AI Extraction Confidence: " + pct + "%");
        } else {
            binding.txtConfidenceScore.setVisibility(View.GONE);
        }

        // Links
        boolean hasReg = opp.getRegistrationUrl() != null && !opp.getRegistrationUrl().isEmpty();
        boolean hasSrc = opp.getSourceUrl() != null && !opp.getSourceUrl().isEmpty();
        binding.cardLinks.setVisibility((hasReg || hasSrc) ? View.VISIBLE : View.GONE);

        if (hasReg) {
            binding.btnOpenRegistration.setVisibility(View.VISIBLE);
            binding.btnOpenRegistration.setOnClickListener(v -> openBrowserUrl(opp.getRegistrationUrl()));
        } else {
            binding.btnOpenRegistration.setVisibility(View.GONE);
        }

        if (hasSrc) {
            binding.btnOpenSource.setVisibility(View.VISIBLE);
            binding.btnOpenSource.setOnClickListener(v -> openBrowserUrl(opp.getSourceUrl()));
        } else {
            binding.btnOpenSource.setVisibility(View.GONE);
        }

        // Description
        binding.txtDescription.setText(opp.getDescription() != null && !opp.getDescription().isEmpty()
                ? opp.getDescription()
                : "No description provided.");
    }

    private void bindHistoryData(List<OpportunityActivityDto> activities) {
        binding.layoutHistoryList.removeAllViews();
        if (activities == null || activities.isEmpty()) {
            binding.lblHistory.setVisibility(View.GONE);
            binding.layoutHistoryList.setVisibility(View.GONE);
            return;
        }

        binding.lblHistory.setVisibility(View.VISIBLE);
        binding.layoutHistoryList.setVisibility(View.VISIBLE);

        for (OpportunityActivityDto act : activities) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_opportunity_card, binding.layoutHistoryList, false);
            TextView txtTitle = itemView.findViewById(R.id.txtOpportunityTitle);
            TextView txtOrg = itemView.findViewById(R.id.txtOrganization);
            TextView chipType = itemView.findViewById(R.id.chipOpportunityType);
            TextView txtDeadline = itemView.findViewById(R.id.txtDeadline);

            chipType.setText(act.getActivityType() != null ? act.getActivityType() : "ACTIVITY");
            txtTitle.setText(act.getDescription() != null ? act.getDescription() : "Status update");
            txtOrg.setText(act.getComment() != null && !act.getComment().isEmpty() ? "Note: " + act.getComment() : "");
            txtDeadline.setText(act.getCreatedAt() != null ? act.getCreatedAt() : "");
            txtDeadline.setTextColor(ContextCompat.getColor(this, R.color.mnesa_text_muted));

            binding.layoutHistoryList.addView(itemView);
        }
    }

    private void showStatusSelectionDialog() {
        String[] statuses = {
                "SAVED", "REVIEWING", "APPLYING", "APPLIED", "WAITING", "SELECTED", "REJECTED", "MISSED", "ARCHIVED"
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle("Select New Status")
                .setItems(statuses, (dialog, which) -> {
                    String selected = statuses[which];
                    viewModel.updateStatus(selected, "Status updated from mobile app");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openBrowserUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Archive").setIcon(R.drawable.ic_archive).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 2, 0, "Restore").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(0, 3, 0, "Delete").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            viewModel.archiveOpportunity();
            return true;
        } else if (item.getItemId() == 2) {
            viewModel.restoreOpportunity();
            return true;
        } else if (item.getItemId() == 3) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Delete Opportunity")
                    .setMessage("Are you sure you want to delete this opportunity? This cannot be undone.")
                    .setPositiveButton("Delete", (d, w) -> {
                        viewModel.deleteOpportunity();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
