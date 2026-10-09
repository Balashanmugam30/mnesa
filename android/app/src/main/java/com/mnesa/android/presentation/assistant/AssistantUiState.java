package com.mnesa.android.presentation.assistant;

import com.mnesa.android.domain.model.AssistantMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AssistantUiState {

    private final boolean loading;
    private final String errorMessage;
    private final List<AssistantMessage> messages;
    private final List<String> promptSuggestions;

    public AssistantUiState(boolean loading,
                            String errorMessage,
                            List<AssistantMessage> messages,
                            List<String> promptSuggestions) {
        this.loading = loading;
        this.errorMessage = errorMessage;
        this.messages = messages != null ? messages : Collections.emptyList();
        this.promptSuggestions = promptSuggestions != null ? promptSuggestions : Collections.emptyList();
    }

    public static AssistantUiState initial() {
        List<String> defaultSuggestions = Arrays.asList(
                "What deadlines do I have this week?",
                "Which high-priority opportunities are pending?",
                "Summarize my active job applications",
                "What competitions should I prepare for?"
        );
        List<AssistantMessage> initialMessages = new ArrayList<>();
        initialMessages.add(AssistantMessage.fromAssistant(
                "Hello! I am your MNESA Personal Assistant. I'm strictly grounded in your saved opportunities, deadlines, and priorities. Ask me anything about your tracked opportunities!",
                Collections.emptyList(),
                Arrays.asList("What deadlines are coming up?", "Show my top priority items")
        ));
        return new AssistantUiState(false, null, initialMessages, defaultSuggestions);
    }

    public boolean isLoading() { return loading; }
    public String getErrorMessage() { return errorMessage; }
    public List<AssistantMessage> getMessages() { return messages; }
    public List<String> getPromptSuggestions() { return promptSuggestions; }
}
