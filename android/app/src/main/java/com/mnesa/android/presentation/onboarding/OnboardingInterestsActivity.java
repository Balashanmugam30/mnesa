package com.mnesa.android.presentation.onboarding;

import android.content.Intent;
import android.view.LayoutInflater;

import com.mnesa.android.core.base.BaseActivity;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.repository.UserRepositoryImpl;
import com.mnesa.android.databinding.ActivityOnboardingInterestsBinding;
import com.mnesa.android.domain.model.UserPreferences;
import com.mnesa.android.presentation.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OnboardingInterestsActivity extends BaseActivity<ActivityOnboardingInterestsBinding> {

    private final CompositeDisposable disposables = new CompositeDisposable();
    private UserRepositoryImpl userRepository;

    @Override
    protected ActivityOnboardingInterestsBinding inflateBinding(LayoutInflater inflater) {
        return ActivityOnboardingInterestsBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        ApiClient apiClient = ApiClient.getInstance(this);
        userRepository = new UserRepositoryImpl(apiClient.getUserApiService(), apiClient.getTokenManager());

        binding.btnFinishSetup.setOnClickListener(v -> savePreferencesAndNavigate());
    }

    @Override
    protected void observeViewModel() {
        // Direct flow completion
    }

    private void savePreferencesAndNavigate() {
        List<String> interests = new ArrayList<>();
        if (binding.chipInternships.isChecked()) interests.add("INTERNSHIP");
        if (binding.chipJobs.isChecked()) interests.add("JOB");
        if (binding.chipHackathons.isChecked()) interests.add("HACKATHON");
        if (binding.chipScholarships.isChecked()) interests.add("SCHOLARSHIP");
        if (binding.chipCompetitions.isChecked()) interests.add("COMPETITION");
        if (binding.chipEvents.isChecked()) interests.add("EVENT");
        if (binding.chipCourses.isChecked()) interests.add("COURSE");
        if (binding.chipGrants.isChecked()) interests.add("GRANT");

        String timing = "STANDARD";
        if (binding.rbAggressive.isChecked()) {
            timing = "AGGRESSIVE";
        } else if (binding.rbImmediate.isChecked()) {
            timing = "IMMEDIATE";
        }

        UserPreferences preferences = new UserPreferences(interests, timing, true, true);

        disposables.add(userRepository.updatePreferences(preferences)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        saved -> navigateToMain(),
                        error -> navigateToMain() // Fallback to proceed even if offline
                ));
    }

    private void navigateToMain() {
        Intent intent = new Intent(OnboardingInterestsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        disposables.clear();
        super.onDestroy();
    }
}
