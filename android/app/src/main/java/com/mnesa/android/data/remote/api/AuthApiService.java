package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/register")
    Single<ApiResponseDto<AuthResponseDto>> register(@Body RegisterRequestDto request);

    @POST("auth/login")
    Single<ApiResponseDto<AuthResponseDto>> login(@Body LoginRequestDto request);

    @POST("auth/refresh")
    Call<ApiResponseDto<AuthResponseDto>> refresh(@Body RefreshTokenRequestDto request);

    @POST("auth/logout")
    Completable logout(@Body RefreshTokenRequestDto request);

    @POST("auth/google")
    Single<ApiResponseDto<AuthResponseDto>> googleSignIn(@Body GoogleAuthRequestDto request);

    @POST("auth/forgot-password")
    Completable forgotPassword(@Body ForgotPasswordRequestDto request);
}
