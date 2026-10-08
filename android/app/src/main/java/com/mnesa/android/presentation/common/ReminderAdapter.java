package com.mnesa.android.presentation.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.mnesa.android.core.utils.DateTimeUtils;
import com.mnesa.android.databinding.ItemReminderCardBinding;
import com.mnesa.android.domain.model.Reminder;

import java.util.Objects;

/**
 * Reusable RecyclerView adapter displaying reminders in the Reminders tab.
 */
public class ReminderAdapter extends ListAdapter<Reminder, ReminderAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Reminder reminder);
    }

    private final OnItemClickListener listener;

    private static final DiffUtil.ItemCallback<Reminder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getTriggerTimestamp(), newItem.getTriggerTimestamp()) &&
                    Objects.equals(oldItem.getStatus(), newItem.getStatus());
        }
    };

    public ReminderAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderCardBinding binding = ItemReminderCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReminderCardBinding binding;

        ViewHolder(ItemReminderCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Reminder item) {
            binding.txtReminderTitle.setText(item.getTitle());
            binding.txtReminderCadence.setText(item.getReminderType().replace("_", " "));
            binding.txtReminderStatus.setText(item.getStatus());

            if (item.getTriggerTimestamp() > 0) {
                binding.txtTriggerTime.setText("Trigger: " + DateTimeUtils.formatRelativeDeadline(item.getTriggerTimestamp()));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
