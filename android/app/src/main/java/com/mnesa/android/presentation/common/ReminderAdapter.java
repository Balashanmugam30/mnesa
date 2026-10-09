package com.mnesa.android.presentation.common;

import android.view.LayoutInflater;
import android.view.View;
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
 * Reusable RecyclerView adapter displaying reminders with snooze and dismiss actions.
 */
public class ReminderAdapter extends ListAdapter<Reminder, ReminderAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Reminder reminder);
    }

    public interface OnReminderActionListener {
        void onReminderClick(Reminder reminder);
        void onSnoozeClick(Reminder reminder);
        void onDismissClick(Reminder reminder);
    }

    private final OnReminderActionListener actionListener;

    private static final DiffUtil.ItemCallback<Reminder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Reminder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    oldItem.getEffectiveTriggerTime() == newItem.getEffectiveTriggerTime() &&
                    Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                    oldItem.getSnoozeCount() == newItem.getSnoozeCount();
        }
    };

    public ReminderAdapter(OnItemClickListener listener) {
        this(new OnReminderActionListener() {
            @Override
            public void onReminderClick(Reminder reminder) {
                if (listener != null) listener.onItemClick(reminder);
            }
            @Override
            public void onSnoozeClick(Reminder reminder) {}
            @Override
            public void onDismissClick(Reminder reminder) {}
        });
    }

    public ReminderAdapter(OnReminderActionListener actionListener) {
        super(DIFF_CALLBACK);
        this.actionListener = actionListener;
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
            binding.txtReminderCadence.setText(item.getReminderType());
            binding.txtReminderStatus.setText(item.getStatus());

            long trigger = item.getEffectiveTriggerTime();
            String timeFormatted = DateTimeUtils.formatRelativeTimestamp(trigger);
            if (item.getSnoozeUntil() != null && item.getSnoozeUntil() > 0) {
                binding.txtTriggerTime.setText("Snoozed: " + timeFormatted);
            } else {
                binding.txtTriggerTime.setText("Trigger: " + timeFormatted);
            }

            if (item.getSmartReason() != null && !item.getSmartReason().isBlank()) {
                binding.txtSmartReason.setVisibility(View.VISIBLE);
                binding.txtSmartReason.setText(item.getSmartReason());
            } else {
                binding.txtSmartReason.setVisibility(View.GONE);
            }

            // Hide action buttons for completed / cancelled / dismissed reminders
            boolean isActive = "SCHEDULED".equals(item.getStatus()) || "SNOOZED".equals(item.getStatus());
            binding.layoutActions.setVisibility(isActive ? View.VISIBLE : View.GONE);

            binding.btnSnooze.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onSnoozeClick(item);
                }
            });

            binding.btnDismiss.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDismissClick(item);
                }
            });

            binding.getRoot().setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onReminderClick(item);
                }
            });
        }
    }
}
