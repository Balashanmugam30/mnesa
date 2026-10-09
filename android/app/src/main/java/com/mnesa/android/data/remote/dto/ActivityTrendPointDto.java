package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ActivityTrendPointDto {

    @SerializedName("date")
    private String date;

    @SerializedName("count")
    private long count;

    public ActivityTrendPointDto() {}

    public ActivityTrendPointDto(String date, long count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
