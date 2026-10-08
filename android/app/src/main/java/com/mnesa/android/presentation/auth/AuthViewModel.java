package com.mnesa.android.presentation.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.AuthSession;
import com.mnesa.android.domain.repository.AuthRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AuthViewModel extends BaseViewModel {

    public enum Status { IDLE, LOADING, SUCCESS, FORGOT_PASSWORD_SENT, ERROR }

    public static class AuthState {
        private final Status status;
        private final AuthSession session;
        private final String errorMessage;

        private AuthState(Status status, AuthSession session, String errorMessage) {
            this.status = status;
            this.session = session;
            this.errorMessage = errorMessage;
        }

        public static AuthState idle() { return new AuthState(Status.IDLE, null, null); }
        public static AuthState loading() { return new AuthState(Status.LOADING, null, null); }
        public static AuthState success(AuthSession session) { return new AuthState(Status.SUCCESS, session, null); }
        public static AuthState forgotPasswordSent() { return new AuthState(Status.FORGOT_PASSWORD_SENT, null, null); }
        public static AuthState error(String message) { return new AuthState(Status.ERROR, null, message); }

        public Status getStatus() { return status; }
        public AuthSession getSession() { return session; }
        public String getErrorMessage() { return errorMessage; }
    }

    private final AuthRepository authRepository;
    private final MutableLiveData<AuthState> authState = new MutableLiveData<>(AuthState.idle());

    public AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<AuthState> getAuthState() {
        return authState;
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    public void login(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            authState.setValue(AuthState.error("Please enter both email and password"));
            return;
        }

        authState.setValue(AuthState.loading());
        addDisposable(authRepository.login(email.trim(), password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        session -> authState.setValue(AuthState.success(session)),
                        throwable -> authState.setValue(AuthState.error(
                                throwable.getMessage() != null ? throwable.getMessage() : "Authentication failed"
                        ))
                ));
    }

    public void register(String email, String password, String fullName) {
        if (email == null || !email.contains("@")) {
            authState.setValue(AuthState.error("Please enter a valid email address"));
            return;
        }
        if (password == null || password.length() < 8) {
            authState.setValue(AuthState.error("Password must be at least 8 characters"));
            return;
        }

        authState.setValue(AuthState.loading());
        addDisposable(authRepository.register(email.trim(), password, fullName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        session -> authState.setValue(AuthState.success(session)),
                        throwable -> authState.setValue(AuthState.error(
                                throwable.getMessage() != null ? throwable.getMessage() : "Registration failed"
                        ))
                ));
    }

    public void googleSignIn(String idToken) {
        authState.setValue(AuthState.loading());
        addDisposable(authRepository.googleSignIn(idToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        session -> authState.setValue(AuthState.success(session)),
                        throwable -> authState.setValue(AuthState.error(
                                throwable.getMessage() != null ? throwable.getMessage() : "Google Sign-In failed"
                        ))
                ));
    }

    public void forgotPassword(String email) {
        if (email == null || !email.contains("@")) {
            authState.setValue(AuthState.error("Please enter a valid email address"));
            return;
        }

        authState.setValue(AuthState.loading());
        addDisposable(authRepository.forgotPassword(email.trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> authState.setValue(AuthState.forgotPasswordSent()),
                        throwable -> authState.setValue(AuthState.error(
                                throwable.getMessage() != null ? throwable.getMessage() : "Password reset failed"
                        ))
                ));
    }
}
