package com.mnesa.android.core.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

/**
 * Robust network connectivity monitor utilizing Android ConnectivityManager.NetworkCallback.
 * Emits reactive ConnectionState stream for offline-first resilience.
 */
public class ConnectivityMonitor {

    private final ConnectivityManager connectivityManager;
    private final BehaviorSubject<ConnectionState> stateSubject = BehaviorSubject.createDefault(ConnectionState.UNKNOWN);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isRegistered = false;
    private boolean wasOffline = false;

    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            if (wasOffline) {
                // Briefly transition through RECONNECTING for smooth UI feedback
                stateSubject.onNext(ConnectionState.RECONNECTING);
                mainHandler.postDelayed(() -> {
                    wasOffline = false;
                    stateSubject.onNext(ConnectionState.ONLINE);
                }, 1500);
            } else {
                stateSubject.onNext(ConnectionState.ONLINE);
            }
        }

        @Override
        public void onLost(@NonNull Network network) {
            wasOffline = true;
            stateSubject.onNext(ConnectionState.OFFLINE);
        }

        @Override
        public void onUnavailable() {
            wasOffline = true;
            stateSubject.onNext(ConnectionState.OFFLINE);
        }
    };

    public ConnectivityMonitor(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        checkInitialState();
    }

    private void checkInitialState() {
        if (connectivityManager == null) {
            stateSubject.onNext(ConnectionState.UNKNOWN);
            return;
        }

        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                stateSubject.onNext(ConnectionState.ONLINE);
                return;
            }
        }
        wasOffline = true;
        stateSubject.onNext(ConnectionState.OFFLINE);
    }

    public synchronized void startMonitoring() {
        if (isRegistered || connectivityManager == null) return;
        try {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
            isRegistered = true;
        } catch (Exception e) {
            checkInitialState();
        }
    }

    public synchronized void stopMonitoring() {
        if (!isRegistered || connectivityManager == null) return;
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
            isRegistered = false;
        } catch (Exception ignored) {
        }
    }

    public Observable<ConnectionState> getConnectionStateObservable() {
        return stateSubject.distinctUntilChanged();
    }

    public ConnectionState getCurrentState() {
        return stateSubject.getValue();
    }

    public boolean isOnline() {
        return stateSubject.getValue() == ConnectionState.ONLINE;
    }
}
