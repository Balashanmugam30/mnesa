package com.mnesa.android.presentation.assistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.mnesa.android.R;
import com.mnesa.android.data.remote.dto.CitedOpportunityDto;
import com.mnesa.android.databinding.ItemAssistantMessageBinding;
import com.mnesa.android.domain.model.AssistantMessage;

import java.util.ArrayList;
import java.util.List;

public class AssistantMessageAdapter extends RecyclerView.Adapter<AssistantMessageAdapter.MessageViewHolder> {

    public interface OnCitationClickListener {
        void onCitationClick(CitedOpportunityDto citation);
    }

    public interface OnActionClickListener {
        void onActionClick(String action);
    }

    private final List<AssistantMessage> messages = new ArrayList<>();
    private final OnCitationClickListener citationClickListener;
    private final OnActionClickListener actionClickListener;

    public AssistantMessageAdapter(OnCitationClickListener citationClickListener,
                                  OnActionClickListener actionClickListener) {
        this.citationClickListener = citationClickListener;
        this.actionClickListener = actionClickListener;
    }

    public void setMessages(List<AssistantMessage> newMessages) {
        this.messages.clear();
        if (newMessages != null) {
            this.messages.addAll(newMessages);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAssistantMessageBinding binding = ItemAssistantMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemAssistantMessageBinding binding;

        public MessageViewHolder(@NonNull ItemAssistantMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AssistantMessage message) {
            Context context = itemView.getContext();

            if (message.isUser()) {
                binding.layoutUserContainer.setVisibility(View.VISIBLE);
                binding.layoutAssistantContainer.setVisibility(View.GONE);
                binding.txtUserMessage.setText(message.getText());
            } else {
                binding.layoutUserContainer.setVisibility(View.GONE);
                binding.layoutAssistantContainer.setVisibility(View.VISIBLE);
                binding.txtAssistantMessage.setText(message.getText());

                // Bind citations
                binding.chipGroupCitations.removeAllViews();
                List<CitedOpportunityDto> citations = message.getCitations();
                if (citations != null && !citations.isEmpty()) {
                    binding.layoutCitations.setVisibility(View.VISIBLE);
                    for (CitedOpportunityDto citation : citations) {
                        Chip chip = new Chip(context);
                        String label = citation.getTitle();
                        if (citation.getOrganization() != null && !citation.getOrganization().isBlank()) {
                            label = citation.getOrganization() + ": " + label;
                        }
                        chip.setText(label);
                        chip.setChipBackgroundColorResource(R.color.mnesa_primary_light);
                        chip.setTextColor(ContextCompat.getColor(context, R.color.mnesa_primary));
                        chip.setChipStrokeColorResource(R.color.mnesa_border);
                        chip.setChipStrokeWidth(1.0f);
                        chip.setOnClickListener(v -> {
                            if (citationClickListener != null) {
                                citationClickListener.onCitationClick(citation);
                            }
                        });
                        binding.chipGroupCitations.addView(chip);
                    }
                } else {
                    binding.layoutCitations.setVisibility(View.GONE);
                }

                // Bind action suggestions
                binding.chipGroupActions.removeAllViews();
                List<String> actions = message.getActionSuggestions();
                if (actions != null && !actions.isEmpty()) {
                    binding.layoutActionSuggestions.setVisibility(View.VISIBLE);
                    for (String action : actions) {
                        Chip chip = new Chip(context);
                        chip.setText(action);
                        chip.setChipBackgroundColorResource(R.color.mnesa_surface);
                        chip.setTextColor(ContextCompat.getColor(context, R.color.mnesa_text_secondary));
                        chip.setChipStrokeColorResource(R.color.mnesa_border_subtle);
                        chip.setChipStrokeWidth(1.0f);
                        chip.setOnClickListener(v -> {
                            if (actionClickListener != null) {
                                actionClickListener.onActionClick(action);
                            }
                        });
                        binding.chipGroupActions.addView(chip);
                    }
                } else {
                    binding.layoutActionSuggestions.setVisibility(View.GONE);
                }
            }
        }
    }
}
