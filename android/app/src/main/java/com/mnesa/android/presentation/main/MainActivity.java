package com.mnesa.android.presentation.main;

import android.view.LayoutInflater;
import android.view.View;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.repository.OpportunityRepositoryImpl;
import com.mnesa.android.databinding.ActivityMainBinding;

/**
 * Main dashboard screen displaying captured opportunities.
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private MainViewModel viewModel;

    @Override
    protected ActivityMainBinding inflateBinding(LayoutInflater inflater) {
        return ActivityMainBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        setSupportActionBar(binding.toolbar);

        binding.recyclerViewOpportunities.setLayoutManager(new LinearLayoutManager(this));
        
        // Wire dependencies (manual DI foundation ready for Hilt injection)
        AppDatabase database = AppDatabase.getInstance(this);
        OpportunityRepositoryImpl repository = new OpportunityRepositoryImpl(database.opportunityDao());
        viewModel = new MainViewModel(repository);
    }

    @Override
    protected void observeViewModel() {
        viewModel.getOpportunities().observe(this, opportunities -> {
            if (opportunities == null || opportunities.isEmpty()) {
                binding.recyclerViewOpportunities.setVisibility(View.GONE);
                binding.emptyStateLayout.setVisibility(View.VISIBLE);
            } else {
                binding.emptyStateLayout.setVisibility(View.GONE);
                binding.recyclerViewOpportunities.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(com.mnesa.android.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == com.mnesa.android.R.id.action_settings) {
            startActivity(new android.content.Intent(this, com.mnesa.android.presentation.settings.SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
