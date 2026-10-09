package com.mnesa.android.domain.model;

import com.mnesa.android.data.remote.dto.CitedOpportunityDto;

import java.util.ArrayList;
import java.util.List;

public class AssistantMessage {

    private final String id;
    private final boolean user;
    private final String text;
    private final long timestamp;
    private final List<CitedOpportunityDto> citations;
    private final List<String> actionSuggestions;

    public AssistantMessage(String id, boolean user, String text, long timestamp,
                            List<CitedOpportunityDto> citations, List<String> actionSuggestions) {
        this.id = id;
        this.user = user;
        this.text = text;
        this.timestamp = timestamp;
        this.citations = citations != null ? citations : new ArrayList<>();
        this.actionSuggestions = actionSuggestions != null ? actionSuggestions : new ArrayList<>();
    }

    public static AssistantMessage fromUser(String text) {
        return new AssistantMessage(
                java.util.UUID.randomUUID().toString(),
                true,
                text,
                System.currentTimeMillis(),
                null,
                null
        );
    }

    public static AssistantMessage fromAssistant(String text,
                                                 List<CitedOpportunityDto> citations,
                                                 List<String> actionSuggestions) {
        return new AssistantMessage(
                java.util.UUID.randomUUID().toString(),
                false,
                text,
                System.currentTimeMillis(),
                citations,
                actionSuggestions
        );
    }

    public String getId() { return id; }
    public boolean isUser() { return user; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
    public List<CitedOpportunityDto> getCitations() { return citations; }
    public List<String> getActionSuggestions() { return actionSuggestions; }
}
