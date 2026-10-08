package com.mnesa.android.presentation.main;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.mnesa.android.R;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.network.ConnectionState;
import com.mnesa.android.core.network.ConnectivityMonitor;
import com.mnesa.android.core.sync.SyncScheduler;
import com.mnesa.android.databinding.ActivityMainBinding;
import com.mnesa.android.presentation.home.HomeFragment;
import com.mnesa.android.presentation.opportunities.OpportunitiesFragment;
import com.mnesa.android.presentation.profile.ProfileFragment;
import com.mnesa.android.presentation.reminders.RemindersFragment;
import com.mnesa.android.presentation.settings.SettingsActivity;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * Main application shell hosting BottomNavigationView, tab fragments,
 * and reactive non-blocking offline connectivity banner.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private ConnectivityMonitor connectivityMonitor;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment opportunitiesFragment = new OpportunitiesFragment();
    private final Fragment remindersFragment = new RemindersFragment();
    private final Fragment profileFragment = new ProfileFragment();

    @Override
    protected ActivityMainBinding inflateBinding(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        setSupportActionBar(binding.toolbar);

        // Setup Bottom Navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.app_name);
                    getSupportActionBar().setSubtitle(R.string.app_tagline);
                }
                return true;
            } else if (itemId == R.id.nav_opportunities) {
                switchFragment(opportunitiesFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.title_opportunities);
                    getSupportActionBar().setSubtitle("Saved & Captured");
                }
                return true;
            } else if (itemId == R.id.nav_reminders) {
                switchFragment(remindersFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.title_reminders);
                    getSupportActionBar().setSubtitle("Proactive Deadline Alerts");
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.nav_profile);
                    getSupportActionBar().setSubtitle("Identity & Local Cache");
                }
                return true;
            }
            return false;
        });

        // Initial tab
        switchFragment(homeFragment);

        // Setup Connectivity Monitor
        connectivityMonitor = new ConnectivityMonitor(this);
        connectivityMonitor.startMonitoring();
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void observeViewModel() {
        disposables.add(
                connectivityMonitor.getConnectionStateObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleConnectionState)
        );
    }

    private void handleConnectionState(ConnectionState state) {
        if (state == ConnectionState.OFFLINE) {
            binding.layoutOfflineBanner.offlineBannerContainer.setVisibility(View.VISIBLE);
            binding.layoutOfflineBanner.offlineBannerContainer.setBackgroundResource(R.drawable.bg_banner_offline);
            binding.layoutOfflineBanner.imgBannerIcon.setImageResource(R.drawable.ic_wifi_off);
            binding.layoutOfflineBanner.imgBannerIcon.setColorFilter(
                    ContextCompat.getColor(this, R.color.mnesa_urgency_critical));
            binding.layoutOfflineBanner.txtBannerTitle.setText(R.string.offline_banner_title);
            binding.layoutOfflineBanner.txtBannerDesc.setText(R.string.offline_banner_desc);
        } else if (state == ConnectionState.RECONNECTING || state == ConnectionState.ONLINE) {
            if (binding.layoutOfflineBanner.offlineBannerContainer.getVisibility() == View.VISIBLE) {
                binding.layoutOfflineBanner.offlineBannerContainer.setBackgroundResource(R.drawable.bg_banner_online);
                binding.layoutOfflineBanner.imgBannerIcon.setImageResource(R.drawable.ic_check_circle);
                binding.layoutOfflineBanner.imgBannerIcon.setColorFilter(
                        ContextCompat.getColor(this, R.color.mnesa_success));
                binding.layoutOfflineBanner.txtBannerTitle.setText(R.string.online_banner_title);
                binding.layoutOfflineBanner.txtBannerDesc.setText(R.string.online_banner_desc);

                // Trigger background sync when reconnected
                SyncScheduler.triggerSync(this);

                handler.postDelayed(() ->
                        binding.layoutOfflineBanner.offlineBannerContainer.setVisibility(View.GONE), 2500);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
        if (connectivityMonitor != null) {
            connectivityMonitor.stopMonitoring();
        }
    }
}
