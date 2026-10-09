package com.mnesa.android.presentation.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.mnesa.android.core.utils.DateTimeUtils;
import com.mnesa.android.databinding.ItemNotificationCardBinding;
import com.mnesa.android.domain.model.NotificationItem;

import java.util.Objects;

/**
 * RecyclerView adapter displaying notification history records with unread tracking and deep links.
 */
public class NotificationAdapter extends ListAdapter<NotificationItem, NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }

    private final OnNotificationClickListener clickListener;

    private static final DiffUtil.ItemCallback<NotificationItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<NotificationItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull NotificationItem oldItem, @NonNull NotificationItem newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NotificationItem oldItem, @NonNull NotificationItem newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle()) &&
                    Objects.equals(oldItem.getBody(), newItem.getBody()) &&
                    Objects.equals(oldItem.getDeliveryStatus(), newItem.getDeliveryStatus()) &&
                    Objects.equals(oldItem.getOpenedAt(), newItem.getOpenedAt());
        }
    };

    public NotificationAdapter(OnNotificationClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationCardBinding binding = ItemNotificationCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationCardBinding binding;

        ViewHolder(ItemNotificationCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NotificationItem item) {
            binding.txtNotificationTitle.setText(item.getTitle());
            binding.txtNotificationBody.setText(item.getBody());
            binding.txtNotificationChannel.setText(item.getChannel());
            binding.txtSentTime.setText(DateTimeUtils.formatRelativeTimestamp(item.getCreatedAt()));

            boolean isUnread = !"OPENED".equalsIgnoreCase(item.getDeliveryStatus()) && item.getOpenedAt() == null;
            binding.viewUnreadDot.setVisibility(isUnread ? View.VISIBLE : View.INVISIBLE);

            boolean hasLink = item.getDeepLinkUri() != null || item.getOpportunityId() != null;
            binding.txtTapHint.setVisibility(hasLink ? View.VISIBLE : View.GONE);

            binding.getRoot().setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onNotificationClick(item);
                }
            });
        }
    }
}
