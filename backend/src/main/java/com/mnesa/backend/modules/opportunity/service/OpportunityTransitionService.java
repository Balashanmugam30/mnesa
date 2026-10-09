package com.mnesa.backend.modules.opportunity.service;

import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.exception.InvalidLifecycleTransitionException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Domain service enforcing valid opportunity lifecycle state transitions.
 */
@Service
public class OpportunityTransitionService {

    private static final Map<OpportunityStatus, Set<OpportunityStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(OpportunityStatus.class);

    static {
        // Temporary intake stages transitioning to user-saved
        ALLOWED_TRANSITIONS.put(OpportunityStatus.CAPTURED, Set.of(
                OpportunityStatus.UNDERSTOOD, OpportunityStatus.SAVED, OpportunityStatus.ARCHIVED));
        ALLOWED_TRANSITIONS.put(OpportunityStatus.PROCESSING, Set.of(
                OpportunityStatus.UNDERSTOOD, OpportunityStatus.SAVED, OpportunityStatus.ARCHIVED));
        ALLOWED_TRANSITIONS.put(OpportunityStatus.UNDERSTOOD, Set.of(
                OpportunityStatus.SAVED, OpportunityStatus.ARCHIVED));

        // Core active lifecycle transitions
        ALLOWED_TRANSITIONS.put(OpportunityStatus.SAVED, Set.of(
                OpportunityStatus.REVIEWING,
                OpportunityStatus.APPLYING,
                OpportunityStatus.APPLIED,
                OpportunityStatus.WAITING,
                OpportunityStatus.SELECTED,
                OpportunityStatus.REJECTED,
                OpportunityStatus.MISSED,
                OpportunityStatus.ARCHIVED
        ));

        ALLOWED_TRANSITIONS.put(OpportunityStatus.REVIEWING, Set.of(
                OpportunityStatus.SAVED,
                OpportunityStatus.APPLYING,
                OpportunityStatus.APPLIED,
                OpportunityStatus.WAITING,
                OpportunityStatus.SELECTED,
                OpportunityStatus.REJECTED,
                OpportunityStatus.MISSED,
                OpportunityStatus.ARCHIVED
        ));

        ALLOWED_TRANSITIONS.put(OpportunityStatus.APPLYING, Set.of(
                OpportunityStatus.SAVED,
                OpportunityStatus.REVIEWING,
                OpportunityStatus.APPLIED,
                OpportunityStatus.WAITING,
                OpportunityStatus.SELECTED,
                OpportunityStatus.REJECTED,
                OpportunityStatus.MISSED,
                OpportunityStatus.ARCHIVED
        ));

        ALLOWED_TRANSITIONS.put(OpportunityStatus.APPLIED, Set.of(
                OpportunityStatus.WAITING,
                OpportunityStatus.SELECTED,
                OpportunityStatus.REJECTED,
                OpportunityStatus.MISSED,
                OpportunityStatus.APPLYING,
                OpportunityStatus.ARCHIVED
        ));

        ALLOWED_TRANSITIONS.put(OpportunityStatus.WAITING, Set.of(
                OpportunityStatus.SELECTED,
                OpportunityStatus.REJECTED,
                OpportunityStatus.MISSED,
                OpportunityStatus.APPLYING,
                OpportunityStatus.ARCHIVED
        ));

        ALLOWED_TRANSITIONS.put(OpportunityStatus.SELECTED, Set.of(
                OpportunityStatus.ARCHIVED,
                OpportunityStatus.SAVED
        ));

        ALLOWED_TRANSITIONS.put(OpportunityStatus.REJECTED, Set.of(
                OpportunityStatus.ARCHIVED,
                OpportunityStatus.REVIEWING,
                OpportunityStatus.SAVED
        ));

        ALLOWED_TRANSITIONS.put(OpportunityStatus.MISSED, Set.of(
                OpportunityStatus.ARCHIVED,
                OpportunityStatus.REVIEWING,
                OpportunityStatus.SAVED
        ));

        // From ARCHIVED: can restore to previous active status
        ALLOWED_TRANSITIONS.put(OpportunityStatus.ARCHIVED, Set.of(
                OpportunityStatus.SAVED,
                OpportunityStatus.REVIEWING,
                OpportunityStatus.APPLYING,
                OpportunityStatus.APPLIED,
                OpportunityStatus.WAITING,
                OpportunityStatus.SELECTED,
                OpportunityStatus.REJECTED,
                OpportunityStatus.MISSED
        ));
    }

    public boolean canTransition(OpportunityStatus from, OpportunityStatus to) {
        if (from == to) {
            return true;
        }
        Set<OpportunityStatus> targets = ALLOWED_TRANSITIONS.get(from);
        return targets != null && targets.contains(to);
    }

    public void validateTransition(OpportunityStatus from, OpportunityStatus to) {
        if (!canTransition(from, to)) {
            throw new InvalidLifecycleTransitionException(from, to);
        }
    }
}
