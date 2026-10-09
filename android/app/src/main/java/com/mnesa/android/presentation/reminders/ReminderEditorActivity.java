package com.mnesa.android.presentation.reminders;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.mnesa.android.R;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.core.utils.DateTimeUtils;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.remote.dto.CreateReminderRequestDto;
import com.mnesa.android.data.remote.dto.ReminderSuggestionDto;
import com.mnesa.android.data.repository.ReminderRepositoryImpl;
import com.mnesa.android.databinding.ActivityReminderEditorBinding;
import com.mnesa.android.databinding.ItemSuggestionCardBinding;
import com.mnesa.android.domain.model.Reminder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Activity for creating or editing an opportunity reminder with smart suggestion support.
 */
public class ReminderEditorActivity extends AppCompatActivity {

    public static final String EXTRA_OPPORTUNITY_ID = "extra_opportunity_id";
    public static final String EXTRA_OPPORTUNITY_TITLE = "extra_opportunity_title";
    public static final String EXTRA_REMINDER_ID = "extra_reminder_id";

    private ActivityReminderEditorBinding binding;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private ReminderRepositoryImpl reminderRepository;
    private String userId;
    private String opportunityId;
    private String opportunityTitle;
    private String reminderId;

    private Calendar scheduledCalendar = Calendar.getInstance();
    private String selectedReminderType = "CUSTOM";
    private String selectedSmartReason = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppDatabase db = AppDatabase.getInstance(this);
        SecureTokenManager tokenManager = new SecureTokenManager(this);
        userId = tokenManager.getUserId();
        if (userId == null || userId.isEmpty()) userId = "default_user";

        ApiClient apiClient = ApiClient.getInstance(this);
        reminderRepository = new ReminderRepositoryImpl(db.reminderDao(), apiClient.getReminderApiService());

        opportunityId = getIntent().getStringExtra(EXTRA_OPPORTUNITY_ID);
        opportunityTitle = getIntent().getStringExtra(EXTRA_OPPORTUNITY_TITLE);
        reminderId = getIntent().getStringExtra(EXTRA_REMINDER_ID);

        // Default scheduled time: tomorrow at 9:00 AM
        scheduledCalendar.add(Calendar.DAY_OF_YEAR, 1);
        scheduledCalendar.set(Calendar.HOUR_OF_DAY, 9);
        scheduledCalendar.set(Calendar.MINUTE, 0);
        scheduledCalendar.set(Calendar.SECOND, 0);

        setupToolbar();
        setupCadenceChips();
        setupDateTimePickers();
        setupOpportunityBanner();
        updateDateTimeSummary();

        if (reminderId != null) {
            loadExistingReminder();
        } else if (opportunityId != null) {
            loadSmartSuggestions();
        }

        binding.btnSaveReminder.setOnClickListener(v -> saveReminder());
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(reminderId != null ? R.string.editor_title_edit : R.string.editor_title_new);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupOpportunityBanner() {
        if (opportunityTitle != null && !opportunityTitle.trim().isEmpty()) {
            binding.cardOpportunityInfo.setVisibility(View.VISIBLE);
            binding.txtOpportunityTitle.setText(opportunityTitle);
        } else if (opportunityId != null) {
            binding.cardOpportunityInfo.setVisibility(View.VISIBLE);
            binding.txtOpportunityTitle.setText("Opportunity ID: " + opportunityId);
        }
    }

    private void setupCadenceChips() {
        binding.chipGroupType.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipTypePrep) {
                selectedReminderType = "PREPARATION";
            } else if (checkedId == R.id.chipTypeApproaching) {
                selectedReminderType = "APPROACHING_DEADLINE";
            } else if (checkedId == R.id.chipTypeFinal) {
                selectedReminderType = "FINAL_HOURS";
            } else {
                selectedReminderType = "CUSTOM";
            }
        });
    }

    private void setupDateTimePickers() {
        binding.btnPickDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Reminder Date")
                    .setSelection(scheduledCalendar.getTimeInMillis())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                utcCal.setTimeInMillis(selection);

                scheduledCalendar.set(Calendar.YEAR, utcCal.get(Calendar.YEAR));
                scheduledCalendar.set(Calendar.MONTH, utcCal.get(Calendar.MONTH));
                scheduledCalendar.set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH));
                updateDateTimeSummary();
            });

            datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        });

        binding.btnPickTime.setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(scheduledCalendar.get(Calendar.HOUR_OF_DAY))
                    .setMinute(scheduledCalendar.get(Calendar.MINUTE))
                    .setTitleText("Select Reminder Time")
                    .build();

            timePicker.addOnPositiveButtonClickListener(dialog -> {
                scheduledCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                scheduledCalendar.set(Calendar.MINUTE, timePicker.getMinute());
                updateDateTimeSummary();
            });

            timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
        });
    }

    private void updateDateTimeSummary() {
        String formatted = DateTimeUtils.formatFullDate(scheduledCalendar.getTimeInMillis());
        binding.txtScheduledSummary.setText("Scheduled: " + formatted);
    }

    private void loadSmartSuggestions() {
        disposables.add(
                reminderRepository.getReminderSuggestions(opportunityId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::renderSuggestions, throwable -> {})
        );
    }

    private void renderSuggestions(List<ReminderSuggestionDto> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) {
            binding.cardSuggestions.setVisibility(View.GONE);
            return;
        }

        binding.cardSuggestions.setVisibility(View.VISIBLE);
        binding.layoutSuggestionsList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (ReminderSuggestionDto s : suggestions) {
            ItemSuggestionCardBinding itemBinding = ItemSuggestionCardBinding.inflate(inflater, binding.layoutSuggestionsList, false);
            itemBinding.txtSuggestionTitle.setText(s.getTitle());
            itemBinding.txtSuggestionReason.setText(s.getReason());

            long triggerMillis = System.currentTimeMillis();
            if (s.getSuggestedScheduledAt() != null) {
                try {
                    triggerMillis = Instant.parse(s.getSuggestedScheduledAt()).toEpochMilli();
                } catch (Exception ignored) {}
            }
            itemBinding.txtSuggestionTime.setText(DateTimeUtils.formatRelativeTimestamp(triggerMillis));

            final long targetTime = triggerMillis;
            itemBinding.btnApplySuggestion.setOnClickListener(v -> applySuggestion(s, targetTime));
            binding.layoutSuggestionsList.addView(itemBinding.getRoot());
        }
    }

    private void applySuggestion(ReminderSuggestionDto suggestion, long triggerMillis) {
        binding.etTitle.setText(suggestion.getTitle());
        scheduledCalendar.setTimeInMillis(triggerMillis);
        updateDateTimeSummary();
        selectedSmartReason = suggestion.getReason();

        if ("PREPARATION".equalsIgnoreCase(suggestion.getReminderType())) {
            binding.chipTypePrep.setChecked(true);
            selectedReminderType = "PREPARATION";
        } else if ("APPROACHING_DEADLINE".equalsIgnoreCase(suggestion.getReminderType())) {
            binding.chipTypeApproaching.setChecked(true);
            selectedReminderType = "APPROACHING_DEADLINE";
        } else if ("FINAL_HOURS".equalsIgnoreCase(suggestion.getReminderType())) {
            binding.chipTypeFinal.setChecked(true);
            selectedReminderType = "FINAL_HOURS";
        }

        Toast.makeText(this, "Applied smart suggestion schedule", Toast.LENGTH_SHORT).show();
    }

    private void loadExistingReminder() {
        disposables.add(
                reminderRepository.getReminderById(reminderId, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(reminder -> {
                            binding.etTitle.setText(reminder.getTitle());
                            if (reminder.getNotes() != null) {
                                binding.etNotes.setText(reminder.getNotes());
                            }
                            scheduledCalendar.setTimeInMillis(reminder.getEffectiveTriggerTime());
                            updateDateTimeSummary();
                        }, throwable -> {})
        );
    }

    private void saveReminder() {
        String title = binding.etTitle.getText() != null ? binding.etTitle.getText().toString().trim() : "";
        if (title.isEmpty()) {
            binding.tilTitle.setError("Title is required");
            return;
        }
        binding.tilTitle.setError(null);

        String notes = binding.etNotes.getText() != null ? binding.etNotes.getText().toString().trim() : "";
        long triggerTime = scheduledCalendar.getTimeInMillis();
        String scheduledIso = Instant.ofEpochMilli(triggerTime).toString();

        CreateReminderRequestDto requestDto = new CreateReminderRequestDto(
                opportunityId,
                title,
                notes,
                selectedReminderType,
                scheduledIso,
                "UTC",
                selectedSmartReason
        );

        binding.btnSaveReminder.setEnabled(false);

        disposables.add(
                reminderRepository.createReminderRemote(opportunityId != null ? opportunityId : "", requestDto, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(reminder -> {
                            Toast.makeText(ReminderEditorActivity.this, "Reminder scheduled successfully", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }, throwable -> {
                            binding.btnSaveReminder.setEnabled(true);
                            Toast.makeText(ReminderEditorActivity.this, "Scheduled locally: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
        binding = null;
    }
}
