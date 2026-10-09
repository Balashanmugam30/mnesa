package com.mnesa.android.core.network;

import android.content.Context;

import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.remote.api.AuthApiService;
import com.mnesa.android.data.remote.api.UserApiService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static volatile ApiClient instance;

    private final Retrofit retrofit;
    private final AuthApiService authApiService;
    private final UserApiService userApiService;
    private final com.mnesa.android.data.remote.api.IntakeApiService intakeApiService;
    private final com.mnesa.android.data.remote.api.OpportunityApiService opportunityApiService;
    private final com.mnesa.android.data.remote.api.ReminderApiService reminderApiService;
    private final com.mnesa.android.data.remote.api.AssistantApiService assistantApiService;
    private final com.mnesa.android.data.remote.api.InsightsApiService insightsApiService;
    private final SecureTokenManager tokenManager;

    private ApiClient(Context context) {
        this.tokenManager = new SecureTokenManager(context.getApplicationContext());

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        AuthInterceptor authInterceptor = new AuthInterceptor(tokenManager);
        TokenAuthenticator authenticator = new TokenAuthenticator(tokenManager);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(ApiConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(ApiConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .authenticator(authenticator)
                .build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        this.authApiService = retrofit.create(AuthApiService.class);
        this.userApiService = retrofit.create(UserApiService.class);
        this.intakeApiService = retrofit.create(com.mnesa.android.data.remote.api.IntakeApiService.class);
        this.opportunityApiService = retrofit.create(com.mnesa.android.data.remote.api.OpportunityApiService.class);
        this.reminderApiService = retrofit.create(com.mnesa.android.data.remote.api.ReminderApiService.class);
        this.assistantApiService = retrofit.create(com.mnesa.android.data.remote.api.AssistantApiService.class);
        this.insightsApiService = retrofit.create(com.mnesa.android.data.remote.api.InsightsApiService.class);

        authenticator.setAuthApiService(authApiService);
    }

    public static ApiClient getInstance(Context context) {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient(context);
                }
            }
        }
        return instance;
    }

    public AuthApiService getAuthApiService() {
        return authApiService;
    }

    public UserApiService getUserApiService() {
        return userApiService;
    }

    public com.mnesa.android.data.remote.api.IntakeApiService getIntakeApiService() {
        return intakeApiService;
    }

    public com.mnesa.android.data.remote.api.OpportunityApiService getOpportunityApiService() {
        return opportunityApiService;
    }

    public com.mnesa.android.data.remote.api.ReminderApiService getReminderApiService() {
        return reminderApiService;
    }

    public com.mnesa.android.data.remote.api.AssistantApiService getAssistantApiService() {
        return assistantApiService;
    }

    public com.mnesa.android.data.remote.api.InsightsApiService getInsightsApiService() {
        return insightsApiService;
    }

    public SecureTokenManager getTokenManager() {
        return tokenManager;
    }

    public SecureTokenManager tokenManager() {
        return tokenManager;
    }
}
