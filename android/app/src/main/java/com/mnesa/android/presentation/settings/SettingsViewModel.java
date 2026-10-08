package com.mnesa.android.presentation.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.User;
import com.mnesa.android.domain.model.UserPreferences;
import com.mnesa.android.domain.repository.AuthRepository;
import com.mnesa.android.domain.repository.UserRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsViewModel extends BaseViewModel {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<UserPreferences> userPreferences = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> accountDeleted = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    public SettingsViewModel(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<User> getUserProfile() { return userProfile; }
    public LiveData<UserPreferences> getUserPreferences() { return userPreferences; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }
    public LiveData<Boolean> getAccountDeleted() { return accountDeleted; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    public void loadProfileAndPreferences() {
        loading.setValue(true);
        addDisposable(userRepository.getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            userProfile.setValue(user);
                            loadPreferences();
                        },
                        throwable -> {
                            loading.setValue(false);
                            // Fallback to locally cached info if offline
                            userProfile.setValue(new User(
                                    null,
                                    authRepository.getCurrentUserEmail(),
                                    authRepository.getCurrentUserName(),
                                    "USER",
                                    "ACTIVE"
                            ));
                        }
                ));
    }

    private void loadPreferences() {
        addDisposable(userRepository.getPreferences()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        prefs -> {
                            loading.setValue(false);
                            userPreferences.setValue(prefs);
                        },
                        throwable -> loading.setValue(false)
                ));
    }

    public void updatePreferences(UserPreferences preferences) {
        loading.setValue(true);
        addDisposable(userRepository.updatePreferences(preferences)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        updated -> {
                            loading.setValue(false);
                            userPreferences.setValue(updated);
                            successMessage.setValue("Preferences updated successfully");
                        },
                        throwable -> {
                            loading.setValue(false);
                            errorMessage.setValue(throwable.getMessage() != null ? throwable.getMessage() : "Failed to update preferences");
                        }
                ));
    }

    public void logout() {
        loading.setValue(true);
        addDisposable(authRepository.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            loading.setValue(false);
                            loggedOut.setValue(true);
                        },
                        throwable -> {
                            loading.setValue(false);
                            loggedOut.setValue(true);
                        }
                ));
    }

    public void deleteAccount() {
        loading.setValue(true);
        addDisposable(userRepository.deleteAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            loading.setValue(false);
                            accountDeleted.setValue(true);
                        },
                        throwable -> {
                            loading.setValue(false);
                            errorMessage.setValue(throwable.getMessage() != null ? throwable.getMessage() : "Failed to delete account");
                        }
                ));
    }
}
