package com.mnesa.android.core.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Manages authentication tokens and session credentials securely using EncryptedSharedPreferences.
 */
public class SecureTokenManager {

    private static final String TAG = "SecureTokenManager";
    private static final String PREF_FILE_NAME = "mnesa_secure_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PUSH_TOKEN = "fcm_push_token";

    private final SharedPreferences sharedPreferences;

    public SecureTokenManager(Context context) {
        SharedPreferences prefs = null;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.w(TAG, "Hardware keystore unavailable, falling back to standard preferences: " + e.getMessage());
            prefs = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        }
        this.sharedPreferences = prefs;
    }

    public synchronized void saveSession(String accessToken, String refreshToken, String userId, String email, String name) {
        sharedPreferences.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, name)
                .apply();
    }

    public synchronized void saveAccessToken(String accessToken) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public synchronized String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public synchronized String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public synchronized String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public synchronized String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public synchronized String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public synchronized boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.trim().isEmpty();
    }

    public synchronized void savePushToken(String pushToken) {
        sharedPreferences.edit().putString(KEY_PUSH_TOKEN, pushToken).apply();
    }

    public synchronized String getPushToken() {
        return sharedPreferences.getString(KEY_PUSH_TOKEN, null);
    }

    public synchronized void clearSession() {
        sharedPreferences.edit().clear().apply();
    }
}
