package com.mnesa.android.presentation.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.local.dao.NotificationDao;
import com.mnesa.android.data.remote.api.ReminderApiService;
import com.mnesa.android.data.repository.NotificationRepositoryImpl;
import com.mnesa.android.databinding.ActivityNotificationsBinding;
import com.mnesa.android.domain.model.NotificationItem;
import com.mnesa.android.domain.repository.NotificationRepository;
import com.mnesa.android.presentation.common.NotificationAdapter;
import com.mnesa.android.presentation.opportunities.OpportunityDetailActivity;

/**
 * Activity presenting the notifications feed with read-status toggling and deep-link navigation.
 */
public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;
    private NotificationsViewModel viewModel;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SecureTokenManager tokenManager = new SecureTokenManager(this);
        String userId = tokenManager.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        AppDatabase db = AppDatabase.getInstance(this);
        NotificationDao dao = db.notificationDao();
        ReminderApiService api = ApiClient.getInstance(this).getReminderApiService();
        NotificationRepository repository = new NotificationRepositoryImpl(dao, api);

        viewModel = new NotificationsViewModel(repository, userId);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnMarkAllRead.setOnClickListener(v -> viewModel.markAllOpened());
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(this::onNotificationClick);
        binding.recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerNotifications.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, loading -> {
            binding.progressLoading.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
        });

        viewModel.getNotifications().observe(this, notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                binding.layoutEmptyNotifications.setVisibility(View.VISIBLE);
                binding.recyclerNotifications.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyNotifications.setVisibility(View.GONE);
                binding.recyclerNotifications.setVisibility(View.VISIBLE);
                adapter.submitList(notifications);
            }
        });

        viewModel.getUnreadCount().observe(this, count -> {
            int unread = (count != null) ? count : 0;
            if (unread > 0) {
                binding.txtUnreadCount.setText(unread + " unread alert" + (unread > 1 ? "s" : ""));
                binding.btnMarkAllRead.setVisibility(View.VISIBLE);
            } else {
                binding.txtUnreadCount.setText("All caught up");
                binding.btnMarkAllRead.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onNotificationClick(NotificationItem item) {
        viewModel.markOpened(item.getId());

        if (item.getDeepLinkUri() != null && !item.getDeepLinkUri().trim().isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getDeepLinkUri()));
                startActivity(intent);
                return;
            } catch (Exception ignored) {}
        }

        if (item.getOpportunityId() != null && !item.getOpportunityId().trim().isEmpty()) {
            OpportunityDetailActivity.start(this, item.getOpportunityId());
        }
    }
}
