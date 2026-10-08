package com.mnesa.android.presentation.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.domain.repository.SyncRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel powering the authenticated Profile tab.
 */
public class ProfileViewModel extends BaseViewModel {

    private final SecureTokenManager tokenManager;
    private final SyncRepository syncRepository;

    private final MutableLiveData<String> nameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> emailLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> initialsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> pendingSyncLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> signedOutLiveData = new MutableLiveData<>(false);

    public ProfileViewModel(SecureTokenManager tokenManager, SyncRepository syncRepository) {
        this.tokenManager = tokenManager;
        this.syncRepository = syncRepository;
        loadProfile();
    }

    public LiveData<String> getName() {
        return nameLiveData;
    }

    public LiveData<String> getEmail() {
        return emailLiveData;
    }

    public LiveData<String> getInitials() {
        return initialsLiveData;
    }

    public LiveData<Integer> getPendingSync() {
        return pendingSyncLiveData;
    }

    public LiveData<Boolean> getSignedOut() {
        return signedOutLiveData;
    }

    public void loadProfile() {
        String name = tokenManager.getUserName();
        String email = tokenManager.getUserEmail();

        if (name == null || name.trim().isEmpty()) {
            name = "MNESA User";
        }
        if (email == null || email.trim().isEmpty()) {
            email = "authenticated@mnesa.local";
        }

        nameLiveData.setValue(name);
        emailLiveData.setValue(email);

        // Derive initials
        String[] parts = name.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(part.substring(0, 1).toUpperCase());
                if (sb.length() >= 2) break;
            }
        }
        initialsLiveData.setValue(sb.length() > 0 ? sb.toString() : "M");

        // Sync count
        addDisposable(
                syncRepository.getPendingCount()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(pendingSyncLiveData::setValue, throwable -> {})
        );
    }

    public void signOut() {
        tokenManager.clearSession();
        signedOutLiveData.setValue(true);
    }
}
