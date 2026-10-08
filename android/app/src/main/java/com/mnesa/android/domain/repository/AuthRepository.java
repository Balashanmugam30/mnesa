package com.mnesa.android.domain.repository;

import com.mnesa.android.domain.model.AuthSession;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface AuthRepository {

    Single<AuthSession> login(String email, String password);

    Single<AuthSession> register(String email, String password, String fullName);

    Single<AuthSession> googleSignIn(String idToken);

    Completable forgotPassword(String email);

    Completable logout();

    boolean isLoggedIn();

    String getCurrentUserEmail();

    String getCurrentUserName();
}
