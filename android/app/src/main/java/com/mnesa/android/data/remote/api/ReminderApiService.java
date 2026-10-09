package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.*;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface ReminderApiService {

    @GET("api/v1/reminders")
    Single<ApiResponseDto<PageResponseDto<ReminderDto>>> getReminders(
            @Query("filter") String filter,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @GET("api/v1/opportunities/{opportunityId}/reminders")
    Single<ApiResponseDto<List<ReminderDto>>> getOpportunityReminders(
            @Path("opportunityId") String opportunityId
    );

    @POST("api/v1/opportunities/{opportunityId}/reminders")
    Single<ApiResponseDto<ReminderDto>> createReminder(
            @Path("opportunityId") String opportunityId,
            @Body CreateReminderRequestDto request
    );

    @GET("api/v1/opportunities/{opportunityId}/reminder-suggestions")
    Single<ApiResponseDto<List<ReminderSuggestionDto>>> getReminderSuggestions(
            @Path("opportunityId") String opportunityId
    );

    @PUT("api/v1/reminders/{id}")
    Single<ApiResponseDto<ReminderDto>> updateReminder(
            @Path("id") String id,
            @Body UpdateReminderRequestDto request
    );

    @POST("api/v1/reminders/{id}/snooze")
    Single<ApiResponseDto<ReminderDto>> snoozeReminder(
            @Path("id") String id,
            @Body SnoozeReminderRequestDto request
    );

    @POST("api/v1/reminders/{id}/dismiss")
    Single<ApiResponseDto<ReminderDto>> dismissReminder(
            @Path("id") String id
    );

    @DELETE("api/v1/reminders/{id}")
    Completable deleteReminder(
            @Path("id") String id
    );

    @GET("api/v1/notifications")
    Single<ApiResponseDto<PageResponseDto<NotificationRecordDto>>> getNotifications(
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    @POST("api/v1/notifications/{id}/opened")
    Single<ApiResponseDto<NotificationRecordDto>> markNotificationOpened(
            @Path("id") String id
    );

    @GET("api/v1/notifications/unread-count")
    Single<ApiResponseDto<Map<String, Long>>> getUnreadNotificationCount();
}
