package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AssistantQueryRequestDto {

    @SerializedName("question")
    private String question;

    @SerializedName("timezone")
    private String timezone;

    public AssistantQueryRequestDto() {}

    public AssistantQueryRequestDto(String question, String timezone) {
        this.question = question;
        this.timezone = timezone;
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}
