package com.mnesa.backend.modules.assistant.service;

import com.mnesa.backend.modules.ai.client.AiServiceClient;
import com.mnesa.backend.modules.ai.dto.AssistantQueryRequestDto;
import com.mnesa.backend.modules.ai.dto.AssistantQueryResponseDto;
import com.mnesa.backend.modules.ai.dto.AssistantRecordDto;
import com.mnesa.backend.modules.assistant.dto.AssistantQueryRequest;
import com.mnesa.backend.modules.assistant.dto.AssistantQueryResponse;
import com.mnesa.backend.modules.assistant.dto.CitedOpportunityDto;
import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AssistantService {

    private static final Logger log = LoggerFactory.getLogger(AssistantService.class);

    private final OpportunityRepository opportunityRepository;
    private final AiServiceClient aiServiceClient;

    private static final Pattern PROMPT_INJECTION_PATTERN = Pattern.compile(
            "(?i)(ignore\\s+(all\\s+)?(previous|prior|above)\\s+instructions|system\\s*:|drop\\s+table|delete\\s+from|update\\s+\\w+\\s+set|exec\\s*\\()"
    );

    public AssistantService(OpportunityRepository opportunityRepository,
                            AiServiceClient aiServiceClient) {
        this.opportunityRepository = opportunityRepository;
        this.aiServiceClient = aiServiceClient;
    }

    @Transactional(readOnly = true)
    public AssistantQueryResponse askAssistant(UUID userId, AssistantQueryRequest request) {
        String rawQuestion = request.getQuestion();
        String sanitizedQuestion = sanitizeQuestion(rawQuestion);
        String questionLower = sanitizedQuestion.toLowerCase();

        // 1. Identify query intent
        String intent = determineIntent(questionLower);

        // 2. Retrieve user-scoped candidate records based on intent
        List<Opportunity> candidateOpportunities = retrieveCandidateOpportunities(userId, intent, questionLower);

        // 3. Prepare bounded context records for AI provider
        List<AssistantRecordDto> contextRecords = candidateOpportunities.stream()
                .map(opp -> AssistantRecordDto.builder()
                        .id(opp.getId().toString())
                        .title(opp.getTitle())
                        .organization(opp.getOrganization())
                        .category(opp.getOpportunityType() != null ? opp.getOpportunityType().name() : "OTHER")
                        .deadlineAt(opp.getDeadlineAt() != null ? opp.getDeadlineAt().toString() : null)
                        .status(opp.getStatus() != null ? opp.getStatus().name() : "SAVED")
                        .priority(opp.getPriority())
                        .build())
                .collect(Collectors.toList());

        // 4. Try AI Service LLM synthesis
        AssistantQueryRequestDto aiRequest = AssistantQueryRequestDto.builder()
                .query(sanitizedQuestion)
                .contextRecords(contextRecords)
                .userTimezone(request.getTimezone())
                .build();

        AssistantQueryResponseDto aiResponse = null;
        try {
            aiResponse = aiServiceClient.generateAssistantResponse(aiRequest);
        } catch (Exception e) {
            log.warn("AI service call failed during assistant query: {}", e.getMessage());
        }

        // 5. Build final response (with deterministic fallback if AI service unavailable)
        String answer;
        List<String> actionSuggestions = new ArrayList<>();
        Set<String> citedIds = new HashSet<>();

        if (aiResponse != null && aiResponse.getAnswer() != null && !aiResponse.getAnswer().isBlank()) {
            answer = aiResponse.getAnswer();
            if (aiResponse.getCitedOpportunityIds() != null) {
                citedIds.addAll(aiResponse.getCitedOpportunityIds());
            }
            if (aiResponse.getActionSuggestions() != null) {
                actionSuggestions.addAll(aiResponse.getActionSuggestions());
            }
            if (aiResponse.getIntent() != null) {
                intent = aiResponse.getIntent();
            }
        } else {
            // Local deterministic fallback
            answer = generateLocalAnswer(intent, sanitizedQuestion, candidateOpportunities);
            for (Opportunity opp : candidateOpportunities) {
                citedIds.add(opp.getId().toString());
            }
            actionSuggestions = generateLocalActionSuggestions(intent, candidateOpportunities);
        }

        // Map cited IDs to structured CitedOpportunityDto
        Map<String, Opportunity> oppMap = candidateOpportunities.stream()
                .collect(Collectors.toMap(o -> o.getId().toString(), o -> o, (a, b) -> a));

        List<CitedOpportunityDto> citedList = new ArrayList<>();
        for (String idStr : citedIds) {
            Opportunity opp = oppMap.get(idStr);
            if (opp != null) {
                citedList.add(CitedOpportunityDto.builder()
                        .id(opp.getId())
                        .title(opp.getTitle())
                        .organization(opp.getOrganization())
                        .category(opp.getOpportunityType() != null ? opp.getOpportunityType().name() : "OTHER")
                        .deadlineAt(opp.getDeadlineAt())
                        .status(opp.getStatus() != null ? opp.getStatus().name() : "SAVED")
                        .priority(opp.getPriority())
                        .build());
            }
        }

        // If no citations returned by LLM but candidates were used, cite the top candidates
        if (citedList.isEmpty() && !candidateOpportunities.isEmpty()) {
            for (Opportunity opp : candidateOpportunities.subList(0, Math.min(3, candidateOpportunities.size()))) {
                citedList.add(CitedOpportunityDto.builder()
                        .id(opp.getId())
                        .title(opp.getTitle())
                        .organization(opp.getOrganization())
                        .category(opp.getOpportunityType() != null ? opp.getOpportunityType().name() : "OTHER")
                        .deadlineAt(opp.getDeadlineAt())
                        .status(opp.getStatus() != null ? opp.getStatus().name() : "SAVED")
                        .priority(opp.getPriority())
                        .build());
            }
        }

        return AssistantQueryResponse.builder()
                .question(rawQuestion)
                .answer(answer)
                .intent(intent)
                .citedOpportunities(citedList)
                .actionSuggestions(actionSuggestions)
                .build();
    }

    private String sanitizeQuestion(String input) {
        if (input == null) return "";
        String cleaned = PROMPT_INJECTION_PATTERN.matcher(input).replaceAll("[FILTERED]");
        return cleaned.trim();
    }

    private String determineIntent(String q) {
        if (q.contains("deadline") || q.contains("due") || q.contains("expir") || q.contains("soon")) {
            return "UPCOMING_DEADLINES";
        }
        if (q.contains("priority") || q.contains("urgent") || q.contains("important") || q.contains("critical")) {
            return "PRIORITY_ACTIONS";
        }
        if (q.contains("week") || q.contains("schedule") || q.contains("today") || q.contains("tomorrow")) {
            return "WEEKLY_SUMMARY";
        }
        if (q.contains("google") || q.contains("meta") || q.contains("amazon") || q.contains("microsoft") || q.contains("from ") || q.contains("at ")) {
            return "ORGANIZATION_SEARCH";
        }
        if (q.contains("applied") || q.contains("status") || q.contains("submitted")) {
            return "APPLICATION_STATUS";
        }
        return "GENERAL_SUMMARY";
    }

    private List<Opportunity> retrieveCandidateOpportunities(UUID userId, String intent, String q) {
        Instant now = Instant.now();
        switch (intent) {
            case "UPCOMING_DEADLINES":
                return opportunityRepository.findByUserIdAndStatusNotAndDeadlineAtIsNotNullAndDeadlineAtGreaterThanEqualOrderByDeadlineAtAsc(
                        userId, OpportunityStatus.ARCHIVED, now, PageRequest.of(0, 10));

            case "WEEKLY_SUMMARY":
                Instant oneWeekFromNow = now.plus(7, ChronoUnit.DAYS);
                return opportunityRepository.findUpcomingBetween(
                        userId, OpportunityStatus.ARCHIVED, now, oneWeekFromNow, PageRequest.of(0, 10));

            case "PRIORITY_ACTIONS":
                return opportunityRepository.findByUserIdAndStatusNotAndPriorityInOrderByDeadlineAtAsc(
                        userId, OpportunityStatus.ARCHIVED, List.of("URGENT", "HIGH"), PageRequest.of(0, 10));

            case "ORGANIZATION_SEARCH":
                // Extract search term from query (e.g. "google", "meta")
                String[] words = q.split("\\s+");
                String searchWord = "";
                for (String w : words) {
                    if (w.length() > 3 && !List.of("what", "show", "from", "saved", "have", "with", "find").contains(w)) {
                        searchWord = w;
                        break;
                    }
                }
                if (!searchWord.isBlank()) {
                    List<Opportunity> found = opportunityRepository.searchByKeyword(
                            userId, OpportunityStatus.ARCHIVED, searchWord, PageRequest.of(0, 10));
                    if (!found.isEmpty()) return found;
                }
                return opportunityRepository.findTop20ByUserIdAndStatusNotOrderByCreatedAtDesc(userId, OpportunityStatus.ARCHIVED);

            default:
                return opportunityRepository.findTop20ByUserIdAndStatusNotOrderByCreatedAtDesc(userId, OpportunityStatus.ARCHIVED);
        }
    }

    private String generateLocalAnswer(String intent, String question, List<Opportunity> opportunities) {
        if (opportunities.isEmpty()) {
            return "You don't have any saved opportunities matching your query yet. You can capture opportunities by sharing links or screenshots from any app!";
        }

        StringBuilder sb = new StringBuilder();
        switch (intent) {
            case "UPCOMING_DEADLINES":
                sb.append("Here are your upcoming deadlines based on your saved opportunities:\n");
                for (Opportunity o : opportunities.subList(0, Math.min(5, opportunities.size()))) {
                    String org = o.getOrganization() != null ? " at " + o.getOrganization() : "";
                    String dl = o.getDeadlineAt() != null ? " (Deadline: " + o.getDeadlineAt().toString() + ")" : "";
                    sb.append("• **").append(o.getTitle()).append("**").append(org).append(dl).append("\n");
                }
                break;

            case "PRIORITY_ACTIONS":
                sb.append("Here are your highest priority items to act on:\n");
                for (Opportunity o : opportunities.subList(0, Math.min(5, opportunities.size()))) {
                    sb.append("• **").append(o.getTitle()).append("** (Priority: ").append(o.getPriority())
                            .append(") - Status: ").append(o.getStatus()).append("\n");
                }
                break;

            case "WEEKLY_SUMMARY":
                sb.append("Here is your opportunity action plan for this week:\n");
                for (Opportunity o : opportunities.subList(0, Math.min(5, opportunities.size()))) {
                    sb.append("• **").append(o.getTitle()).append("** - Due: ")
                            .append(o.getDeadlineAt() != null ? o.getDeadlineAt() : "Flexible").append("\n");
                }
                break;

            default:
                sb.append("Found ").append(opportunities.size()).append(" relevant opportunities for you:\n");
                for (Opportunity o : opportunities.subList(0, Math.min(5, opportunities.size()))) {
                    String org = o.getOrganization() != null ? " (" + o.getOrganization() + ")" : "";
                    sb.append("• **").append(o.getTitle()).append("**").append(org).append(" - Status: ").append(o.getStatus()).append("\n");
                }
                break;
        }

        return sb.toString().trim();
    }

    private List<String> generateLocalActionSuggestions(String intent, List<Opportunity> opportunities) {
        if (opportunities.isEmpty()) {
            return List.of("Capture an opportunity via Android Share", "Explore opportunities");
        }
        switch (intent) {
            case "UPCOMING_DEADLINES":
                return List.of("Set preparation reminders", "Review application checklist");
            case "PRIORITY_ACTIONS":
                return List.of("Work on highest priority application", "Submit completed materials");
            case "WEEKLY_SUMMARY":
                return List.of("Schedule focus time blocks", "Submit before deadlines");
            default:
                return List.of("View opportunity details", "Check deadlines");
        }
    }
}
