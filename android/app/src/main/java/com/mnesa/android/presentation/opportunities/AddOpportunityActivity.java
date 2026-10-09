package com.mnesa.android.presentation.opportunities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.remote.dto.CreateOpportunityRequestDto;
import com.mnesa.android.data.repository.OpportunityRepositoryImpl;
import com.mnesa.android.databinding.ActivityEditOpportunityBinding;

/**
 * Activity for manually adding a new opportunity with full metadata.
 */
public class AddOpportunityActivity extends BaseActivity<ActivityEditOpportunityBinding> {

    private EditOpportunityViewModel viewModel;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddOpportunityActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected ActivityEditOpportunityBinding inflateBinding(LayoutInflater inflater) {
        return ActivityEditOpportunityBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setTitle("Add Opportunity");
        binding.btnSave.setText("Create Opportunity");

        setupDropdowns();

        AppDatabase db = AppDatabase.getInstance(this);
        SecureTokenManager tokenManager = new SecureTokenManager(this);
        String userId = tokenManager.getUserId();
        OpportunityRepositoryImpl oppRepo = new OpportunityRepositoryImpl(this);

        viewModel = new EditOpportunityViewModel(oppRepo, userId);

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> submitCreateForm());
    }

    private void setupDropdowns() {
        String[] categories = {
                "INTERNSHIP", "JOB", "HACKATHON", "SCHOLARSHIP", "COMPETITION", "EVENT", "CONFERENCE", "COURSE", "OTHER"
        };
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        binding.inputCategory.setAdapter(catAdapter);
        binding.inputCategory.setText("INTERNSHIP", false);

        String[] priorities = {"LOW", "MEDIUM", "HIGH", "URGENT"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities);
        binding.inputPriority.setAdapter(priorityAdapter);
        binding.inputPriority.setText("MEDIUM", false);

        String[] workModes = {"REMOTE", "HYBRID", "ONSITE", "VIRTUAL"};
        ArrayAdapter<String> workModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, workModes);
        binding.inputWorkMode.setAdapter(workModeAdapter);
        binding.inputWorkMode.setText("REMOTE", false);
    }

    @Override
    protected void observeViewModel() {
        viewModel.getIsSaving().observe(this, saving -> {
            binding.progressBar.setVisibility(Boolean.TRUE.equals(saving) ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!Boolean.TRUE.equals(saving));
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSaveSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Opportunity created successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void submitCreateForm() {
        String title = binding.inputTitle.getText() != null ? binding.inputTitle.getText().toString().trim() : "";
        String organization = binding.inputOrganization.getText() != null ? binding.inputOrganization.getText().toString().trim() : "";
        String category = binding.inputCategory.getText() != null ? binding.inputCategory.getText().toString().trim() : "OTHER";
        String priority = binding.inputPriority.getText() != null ? binding.inputPriority.getText().toString().trim() : "MEDIUM";
        String location = binding.inputLocation.getText() != null ? binding.inputLocation.getText().toString().trim() : "";
        String workMode = binding.inputWorkMode.getText() != null ? binding.inputWorkMode.getText().toString().trim() : "";
        String eligibility = binding.inputEligibility.getText() != null ? binding.inputEligibility.getText().toString().trim() : "";
        String effort = binding.inputEstimatedEffort.getText() != null ? binding.inputEstimatedEffort.getText().toString().trim() : "";
        String regUrl = binding.inputRegistrationUrl.getText() != null ? binding.inputRegistrationUrl.getText().toString().trim() : "";
        String srcUrl = binding.inputSourceUrl.getText() != null ? binding.inputSourceUrl.getText().toString().trim() : "";
        String desc = binding.inputDescription.getText() != null ? binding.inputDescription.getText().toString().trim() : "";
        String notes = binding.inputNotes.getText() != null ? binding.inputNotes.getText().toString().trim() : "";

        if (title.isEmpty()) {
            binding.layoutTitle.setError("Title is required");
            return;
        } else {
            binding.layoutTitle.setError(null);
        }

        if (organization.isEmpty()) {
            binding.layoutOrganization.setError("Organization is required");
            return;
        } else {
            binding.layoutOrganization.setError(null);
        }

        CreateOpportunityRequestDto req = new CreateOpportunityRequestDto(
                title, organization, category, desc, srcUrl, regUrl,
                null, null, eligibility, location, workMode, effort, priority, notes, null
        );

        viewModel.createOpportunity(req);
    }
}
