package com.mnesa.android.core.network;

/**
 * Global API constants for mobile networking.
 */
public final class ApiConstants {

    private ApiConstants() {
        // Prevent instantiation
    }

    public static final String BASE_URL = "http://10.0.2.2:8080/api/v1/"; // Default Android emulator host loopback
    public static final long CONNECT_TIMEOUT_SECONDS = 15;
    public static final long READ_TIMEOUT_SECONDS = 30;
    public static final String HEADER_CORRELATION_ID = "X-Correlation-ID";
}
