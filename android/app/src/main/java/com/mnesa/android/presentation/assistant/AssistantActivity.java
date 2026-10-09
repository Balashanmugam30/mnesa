package com.mnesa.android.presentation.assistant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import com.mnesa.android.R;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.data.repository.AssistantRepositoryImpl;
import com.mnesa.android.databinding.ActivityAssistantBinding;
import com.mnesa.android.presentation.opportunities.OpportunityDetailActivity;

import java.util.List;

public class AssistantActivity extends BaseActivity<ActivityAssistantBinding> {

    private AssistantViewModel viewModel;
    private AssistantMessageAdapter adapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, AssistantActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected ActivityAssistantBinding inflateBinding(LayoutInflater inflater) {
        return ActivityAssistantBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        AssistantRepositoryImpl repository = new AssistantRepositoryImpl(this);
        viewModel = new AssistantViewModel(repository);

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerMessages.setLayoutManager(layoutManager);

        adapter = new AssistantMessageAdapter(
                citation -> {
                    if (citation != null && citation.getId() != null) {
                        OpportunityDetailActivity.start(AssistantActivity.this, citation.getId());
                    }
                },
                action -> {
                    if (action != null && !action.isBlank()) {
                        viewModel.sendQuery(action);
                    }
                }
        );
        binding.recyclerMessages.setAdapter(adapter);

        binding.btnSend.setOnClickListener(v -> sendCurrentQuery());

        binding.editQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendCurrentQuery();
                return true;
            }
            return false;
        });
    }

    private void sendCurrentQuery() {
        if (binding.editQuery.getText() != null) {
            String text = binding.editQuery.getText().toString().trim();
            if (!text.isEmpty()) {
                viewModel.sendQuery(text);
                binding.editQuery.setText("");
            }
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel.getUiState().observe(this, state -> {
            if (state == null) return;

            adapter.setMessages(state.getMessages());
            if (adapter.getItemCount() > 0) {
                binding.recyclerMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
            }

            if (state.isLoading()) {
                binding.progressSending.setVisibility(View.VISIBLE);
                binding.btnSend.setVisibility(View.INVISIBLE);
            } else {
                binding.progressSending.setVisibility(View.GONE);
                binding.btnSend.setVisibility(View.VISIBLE);
            }

            // Populate suggestions
            binding.chipGroupSuggestions.removeAllViews();
            List<String> suggestions = state.getPromptSuggestions();
            if (suggestions != null && !suggestions.isEmpty()) {
                binding.scrollSuggestions.setVisibility(View.VISIBLE);
                binding.dividerSuggestions.setVisibility(View.VISIBLE);
                for (String suggestion : suggestions) {
                    Chip chip = new Chip(this);
                    chip.setText(suggestion);
                    chip.setChipBackgroundColorResource(R.color.mnesa_surface_card);
                    chip.setChipStrokeColorResource(R.color.mnesa_border_subtle);
                    chip.setChipStrokeWidth(1.0f);
                    chip.setTextColor(getColor(R.color.mnesa_text_secondary));
                    chip.setOnClickListener(v -> viewModel.sendQuery(suggestion));
                    binding.chipGroupSuggestions.addView(chip);
                }
            } else {
                binding.scrollSuggestions.setVisibility(View.GONE);
                binding.dividerSuggestions.setVisibility(View.GONE);
            }
        });
    }
}
