package com.mnesa.android.presentation.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.mnesa.android.R;
import com.mnesa.android.core.utils.DateTimeUtils;
import com.mnesa.android.databinding.ItemOpportunityCardBinding;
import com.mnesa.android.domain.model.Opportunity;

import java.util.Objects;

/**
 * Reusable RecyclerView adapter displaying opportunities across Home and All Opportunities lists.
 */
public class OpportunityAdapter extends ListAdapter<Opportunity, OpportunityAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Opportunity opportunity);
    }

    private final OnItemClickListener listener;

    private static final DiffUtil.ItemCallback<Opportunity> DIFF_CALLBACK = new DiffUtil.ItemCallback<Opportunity>() {
        @Override
        public boolean areItemsTheSame(@NonNull Opportunity oldItem, @NonNull Opportunity newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Opportunity oldItem, @NonNull Opportunity newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                    Objects.equals(oldItem.getCategory(), newItem.getCategory()) &&
                    Objects.equals(oldItem.getDeadlineTimestamp(), newItem.getDeadlineTimestamp());
        }
    };

    public OpportunityAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOpportunityCardBinding binding = ItemOpportunityCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOpportunityCardBinding binding;

        ViewHolder(ItemOpportunityCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Opportunity item) {
            Context context = binding.getRoot().getContext();

            binding.txtOpportunityTitle.setText(item.getTitle());
            binding.txtOrganization.setText(item.getOrganization());
            binding.chipOpportunityType.setText(item.getCategory() != null ? item.getCategory().toUpperCase() : "OPPORTUNITY");

            if (item.getStatus() != null) {
                binding.chipOpportunityStatus.setVisibility(View.VISIBLE);
                binding.chipOpportunityStatus.setText(item.getStatus().name());
                switch (item.getStatus()) {
                    case SELECTED:
                    case COMPLETED:
                        binding.chipOpportunityStatus.setTextColor(ContextCompat.getColor(context, R.color.mnesa_status_success));
                        break;
                    case REJECTED:
                    case MISSED:
                        binding.chipOpportunityStatus.setTextColor(ContextCompat.getColor(context, R.color.mnesa_urgency_critical));
                        break;
                    case APPLYING:
                    case REVIEWING:
                        binding.chipOpportunityStatus.setTextColor(ContextCompat.getColor(context, R.color.mnesa_primary));
                        break;
                    case APPLIED:
                    case WAITING:
                        binding.chipOpportunityStatus.setTextColor(ContextCompat.getColor(context, R.color.mnesa_urgency_warning));
                        break;
                    default:
                        binding.chipOpportunityStatus.setTextColor(ContextCompat.getColor(context, R.color.mnesa_text_secondary));
                        break;
                }
            } else {
                binding.chipOpportunityStatus.setVisibility(View.GONE);
            }

            if (item.hasDeadline()) {
                binding.txtDeadline.setVisibility(View.VISIBLE);
                long now = System.currentTimeMillis();
                long diffDays = (item.getDeadlineTimestamp() - now) / (1000L * 60 * 60 * 24);

                if (diffDays <= 0) {
                    binding.txtDeadline.setText("Due today");
                    binding.txtDeadline.setTextColor(ContextCompat.getColor(context, R.color.mnesa_urgency_critical));
                } else if (diffDays <= 3) {
                    binding.txtDeadline.setText(diffDays + (diffDays == 1 ? " day left" : " days left"));
                    binding.txtDeadline.setTextColor(ContextCompat.getColor(context, R.color.mnesa_urgency_critical));
                } else if (diffDays <= 7) {
                    binding.txtDeadline.setText(diffDays + " days left");
                    binding.txtDeadline.setTextColor(ContextCompat.getColor(context, R.color.mnesa_urgency_warning));
                } else {
                    binding.txtDeadline.setText(DateTimeUtils.formatRelativeDeadline(item.getDeadlineTimestamp()));
                    binding.txtDeadline.setTextColor(ContextCompat.getColor(context, R.color.mnesa_text_secondary));
                }
            } else {
                binding.txtDeadline.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
