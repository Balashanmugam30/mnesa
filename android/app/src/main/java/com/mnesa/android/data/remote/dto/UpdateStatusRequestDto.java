package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class UpdateStatusRequestDto {

    @SerializedName("status")
    private String status;

    @SerializedName("comment")
    private String comment;

    public UpdateStatusRequestDto() {}

    public UpdateStatusRequestDto(String status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
