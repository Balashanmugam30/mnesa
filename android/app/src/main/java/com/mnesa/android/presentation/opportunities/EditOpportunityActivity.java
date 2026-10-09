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
import com.mnesa.android.data.remote.dto.UpdateOpportunityRequestDto;
import com.mnesa.android.data.repository.OpportunityRepositoryImpl;
import com.mnesa.android.databinding.ActivityEditOpportunityBinding;
import com.mnesa.android.domain.model.Opportunity;

/**
 * Activity for editing opportunity metadata, links, work mode, eligibility, and notes.
 */
public class EditOpportunityActivity extends BaseActivity<ActivityEditOpportunityBinding> {

    public static final String EXTRA_OPPORTUNITY_ID = "extra_opportunity_id";

    private EditOpportunityViewModel viewModel;
    private String opportunityId;

    public static void start(Context context, String opportunityId) {
        Intent intent = new Intent(context, EditOpportunityActivity.class);
        intent.putExtra(EXTRA_OPPORTUNITY_ID, opportunityId);
        context.startActivity(intent);
    }

    @Override
    protected ActivityEditOpportunityBinding inflateBinding(LayoutInflater inflater) {
        return ActivityEditOpportunityBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        opportunityId = getIntent().getStringExtra(EXTRA_OPPORTUNITY_ID);
        if (opportunityId == null || opportunityId.isEmpty()) {
            Toast.makeText(this, "Opportunity ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setTitle("Edit Opportunity");

        setupDropdowns();

        AppDatabase db = AppDatabase.getInstance(this);
        SecureTokenManager tokenManager = new SecureTokenManager(this);
        String userId = tokenManager.getUserId();
        OpportunityRepositoryImpl oppRepo = new OpportunityRepositoryImpl(this);

        viewModel = new EditOpportunityViewModel(oppRepo, userId);

        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> submitEditForm());

        viewModel.loadForEdit(opportunityId);
    }

    private void setupDropdowns() {
        String[] categories = {
                "INTERNSHIP", "JOB", "HACKATHON", "SCHOLARSHIP", "COMPETITION", "EVENT", "CONFERENCE", "COURSE", "OTHER"
        };
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        binding.inputCategory.setAdapter(catAdapter);

        String[] priorities = {"LOW", "MEDIUM", "HIGH", "URGENT"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities);
        binding.inputPriority.setAdapter(priorityAdapter);

        String[] workModes = {"REMOTE", "HYBRID", "ONSITE", "VIRTUAL"};
        ArrayAdapter<String> workModeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, workModes);
        binding.inputWorkMode.setAdapter(workModeAdapter);
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
                Toast.makeText(this, "Opportunity updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getOpportunity().observe(this, this::populateForm);
    }

    private void populateForm(Opportunity opp) {
        if (opp == null) return;

        binding.inputTitle.setText(opp.getTitle());
        binding.inputOrganization.setText(opp.getOrganization());
        binding.inputCategory.setText(opp.getCategory() != null ? opp.getCategory().toUpperCase() : "INTERNSHIP", false);
        binding.inputPriority.setText(opp.getPriority() != null ? opp.getPriority().toUpperCase() : "MEDIUM", false);
        binding.inputLocation.setText(opp.getLocation() != null ? opp.getLocation() : "");
        binding.inputEligibility.setText(opp.getEligibility() != null ? opp.getEligibility() : "");
        binding.inputEstimatedEffort.setText(opp.getEstimatedEffort() != null ? opp.getEstimatedEffort() : "");
        binding.inputRegistrationUrl.setText(opp.getRegistrationUrl() != null ? opp.getRegistrationUrl() : "");
        binding.inputSourceUrl.setText(opp.getSourceUrl() != null ? opp.getSourceUrl() : "");
        binding.inputDescription.setText(opp.getDescription() != null ? opp.getDescription() : "");
    }

    private void submitEditForm() {
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

        UpdateOpportunityRequestDto req = new UpdateOpportunityRequestDto();
        req.setTitle(title);
        req.setOrganization(organization);
        req.setCategory(category);
        req.setPriority(priority);
        req.setLocation(location);
        req.setWorkMode(workMode);
        req.setEligibility(eligibility);
        req.setEstimatedEffort(effort);
        req.setRegistrationUrl(regUrl);
        req.setSourceUrl(srcUrl);
        req.setDescription(desc);
        req.setNotes(notes);

        viewModel.updateOpportunity(opportunityId, req);
    }
}
