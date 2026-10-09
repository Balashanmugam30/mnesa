package com.mnesa.android.presentation.assistant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.AssistantMessage;
import com.mnesa.android.domain.repository.AssistantRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class AssistantViewModel extends BaseViewModel {

    private final AssistantRepository repository;
    private final MutableLiveData<AssistantUiState> uiStateLiveData = new MutableLiveData<>();
    private final List<AssistantMessage> messageList = new ArrayList<>();

    public AssistantViewModel(AssistantRepository repository) {
        this.repository = repository;
        AssistantUiState initial = AssistantUiState.initial();
        messageList.addAll(initial.getMessages());
        uiStateLiveData.setValue(initial);
    }

    public LiveData<AssistantUiState> getUiState() {
        return uiStateLiveData;
    }

    public void sendQuery(String query) {
        if (query == null || query.trim().isBlank()) {
            return;
        }

        String question = query.trim();
        AssistantMessage userMsg = AssistantMessage.fromUser(question);
        messageList.add(userMsg);

        AssistantUiState current = uiStateLiveData.getValue();
        List<String> suggestions = current != null ? current.getPromptSuggestions() : new ArrayList<>();

        uiStateLiveData.setValue(new AssistantUiState(
                true,
                null,
                new ArrayList<>(messageList),
                suggestions
        ));

        String tz = TimeZone.getDefault().getID();

        addDisposable(
                repository.askAssistant(question, tz)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    AssistantMessage assistantMsg = AssistantMessage.fromAssistant(
                                            response.getAnswer(),
                                            response.getCitedOpportunities(),
                                            response.getActionSuggestions()
                                    );
                                    messageList.add(assistantMsg);
                                    uiStateLiveData.setValue(new AssistantUiState(
                                            false,
                                            null,
                                            new ArrayList<>(messageList),
                                            response.getActionSuggestions() != null && !response.getActionSuggestions().isEmpty()
                                                    ? response.getActionSuggestions()
                                                    : suggestions
                                    ));
                                },
                                throwable -> {
                                    String fallbackAnswer = "I'm having trouble connecting right now. Please verify your connection or try again shortly.";
                                    if (throwable.getMessage() != null && !throwable.getMessage().isBlank()) {
                                        fallbackAnswer = "Unable to process query: " + throwable.getMessage();
                                    }
                                    AssistantMessage errMsg = AssistantMessage.fromAssistant(
                                            fallbackAnswer,
                                            null,
                                            null
                                    );
                                    messageList.add(errMsg);
                                    uiStateLiveData.setValue(new AssistantUiState(
                                            false,
                                            throwable.getMessage(),
                                            new ArrayList<>(messageList),
                                            suggestions
                                    ));
                                }
                        )
        );
    }
}
