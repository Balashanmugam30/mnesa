package com.mnesa.android.core.network;

/**
 * Global API constants for mobile networking.
 */
public final class ApiConstants {

    private ApiConstants() {
        // Prevent instantiation
    }

    public static final String BASE_URL = com.mnesa.android.BuildConfig.BASE_URL;
    public static final long CONNECT_TIMEOUT_SECONDS = 60;
    public static final long READ_TIMEOUT_SECONDS = 90;
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
}
