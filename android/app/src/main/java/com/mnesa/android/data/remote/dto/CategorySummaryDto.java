package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CategorySummaryDto {

    @SerializedName("category")
    private String category;

    @SerializedName("count")
    private long count;

    public CategorySummaryDto() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
