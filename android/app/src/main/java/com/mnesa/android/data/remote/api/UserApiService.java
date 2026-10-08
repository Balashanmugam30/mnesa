package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApiService {

    @GET("users/me")
    Single<ApiResponseDto<UserProfileDto>> getProfile();

    @PUT("users/me")
    Single<ApiResponseDto<UserProfileDto>> updateProfile(@Body UpdateProfileRequestDto request);

    @GET("users/me/preferences")
    Single<ApiResponseDto<UserPreferencesDto>> getPreferences();

    @PUT("users/me/preferences")
    Single<ApiResponseDto<UserPreferencesDto>> updatePreferences(@Body UpdatePreferencesRequestDto request);

    @DELETE("users/me")
    Completable deleteAccount();
}
