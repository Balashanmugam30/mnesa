package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AssistantQueryResponseDto {

    @SerializedName("question")
    private String question;

    @SerializedName("answer")
    private String answer;

    @SerializedName("intent")
    private String intent;

    @SerializedName("citedOpportunities")
    private List<CitedOpportunityDto> citedOpportunities = new ArrayList<>();

    @SerializedName("actionSuggestions")
    private List<String> actionSuggestions = new ArrayList<>();

    public AssistantQueryResponseDto() {}

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }

    public List<CitedOpportunityDto> getCitedOpportunities() { return citedOpportunities; }
    public void setCitedOpportunities(List<CitedOpportunityDto> citedOpportunities) { this.citedOpportunities = citedOpportunities; }

    public List<String> getActionSuggestions() { return actionSuggestions; }
    public void setActionSuggestions(List<String> actionSuggestions) { this.actionSuggestions = actionSuggestions; }
}
