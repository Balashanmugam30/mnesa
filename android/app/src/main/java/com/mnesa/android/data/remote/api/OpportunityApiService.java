package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;

public interface OpportunityApiService {

    @GET("api/v1/opportunities")
    Single<ApiResponseDto<PageResponseDto<OpportunitySummaryDto>>> searchOpportunities(
            @Query("search") String search,
            @Query("category") String category,
            @Query("status") String status,
            @Query("priority") String priority,
            @Query("deadlineBefore") Long deadlineBefore,
            @Query("deadlineAfter") Long deadlineAfter,
            @Query("organization") String organization,
            @Query("location") String location,
            @Query("tag") String tag,
            @Query("archived") Boolean archived,
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("sort") String sort
    );

    @GET("api/v1/opportunities/{id}")
    Single<ApiResponseDto<OpportunityDto>> getOpportunityById(@Path("id") String id);

    @POST("api/v1/opportunities")
    Single<ApiResponseDto<OpportunityDto>> createOpportunity(@Body CreateOpportunityRequestDto request);

    @PUT("api/v1/opportunities/{id}")
    Single<ApiResponseDto<OpportunityDto>> updateOpportunity(@Path("id") String id, @Body UpdateOpportunityRequestDto request);

    @PATCH("api/v1/opportunities/{id}/status")
    Single<ApiResponseDto<OpportunityDto>> updateStatus(@Path("id") String id, @Body UpdateStatusRequestDto request);

    @POST("api/v1/opportunities/{id}/archive")
    Single<ApiResponseDto<OpportunityDto>> archiveOpportunity(@Path("id") String id);

    @POST("api/v1/opportunities/{id}/restore")
    Single<ApiResponseDto<OpportunityDto>> restoreOpportunity(@Path("id") String id);

    @DELETE("api/v1/opportunities/{id}")
    Completable deleteOpportunity(@Path("id") String id);

    @GET("api/v1/opportunities/{id}/history")
    Single<ApiResponseDto<List<OpportunityActivityDto>>> getOpportunityHistory(@Path("id") String id);

    @GET("api/v1/home")
    Single<ApiResponseDto<HomeDashboardDto>> getHomeDashboard();

    @GET("api/v1/categories")
    Single<ApiResponseDto<List<CategorySummaryDto>>> getCategories();

    @GET("api/v1/tags")
    Single<ApiResponseDto<List<TagDto>>> getTags();
}
