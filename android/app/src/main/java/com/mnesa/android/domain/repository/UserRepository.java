package com.mnesa.android.domain.repository;

import com.mnesa.android.domain.model.User;
import com.mnesa.android.domain.model.UserPreferences;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {

    Single<User> getProfile();

    Single<User> updateProfile(String fullName);

    Single<UserPreferences> getPreferences();

    Single<UserPreferences> updatePreferences(UserPreferences preferences);

    Completable deleteAccount();
}
