package com.mnesa.android.core.network;

/**
 * State representing network connectivity for offline-first behavior.
 */
public enum ConnectionState {
    ONLINE,
    OFFLINE,
    RECONNECTING,
    UNKNOWN
}
