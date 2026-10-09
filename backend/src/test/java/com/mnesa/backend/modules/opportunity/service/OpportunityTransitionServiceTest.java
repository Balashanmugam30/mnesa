package com.mnesa.backend.modules.opportunity.service;

import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.exception.InvalidLifecycleTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpportunityTransitionServiceTest {

    private OpportunityTransitionService transitionService;

    @BeforeEach
    void setUp() {
        transitionService = new OpportunityTransitionService();
    }

    @Test
    @DisplayName("Same status transition is always valid (no-op)")
    void sameStatusIsValid() {
        assertTrue(transitionService.canTransition(OpportunityStatus.SAVED, OpportunityStatus.SAVED));
        assertDoesNotThrow(() -> transitionService.validateTransition(OpportunityStatus.SAVED, OpportunityStatus.SAVED));
    }

    @Test
    @DisplayName("SAVED status can transition to active states and archive")
    void savedTransitions() {
        assertTrue(transitionService.canTransition(OpportunityStatus.SAVED, OpportunityStatus.REVIEWING));
        assertTrue(transitionService.canTransition(OpportunityStatus.SAVED, OpportunityStatus.APPLYING));
        assertTrue(transitionService.canTransition(OpportunityStatus.SAVED, OpportunityStatus.APPLIED));
        assertTrue(transitionService.canTransition(OpportunityStatus.SAVED, OpportunityStatus.ARCHIVED));
        assertTrue(transitionService.canTransition(OpportunityStatus.SAVED, OpportunityStatus.MISSED));
        assertTrue(transitionService.canTransition(OpportunityStatus.SAVED, OpportunityStatus.REJECTED));
    }

    @Test
    @DisplayName("APPLYING can transition to APPLIED, WAITING, SELECTED, REJECTED, ARCHIVED")
    void applyingTransitions() {
        assertTrue(transitionService.canTransition(OpportunityStatus.APPLYING, OpportunityStatus.APPLIED));
        assertTrue(transitionService.canTransition(OpportunityStatus.APPLYING, OpportunityStatus.WAITING));
        assertTrue(transitionService.canTransition(OpportunityStatus.APPLYING, OpportunityStatus.ARCHIVED));
    }

    @Test
    @DisplayName("ARCHIVED can transition back to valid active states (restore)")
    void archivedTransitions() {
        assertTrue(transitionService.canTransition(OpportunityStatus.ARCHIVED, OpportunityStatus.SAVED));
        assertTrue(transitionService.canTransition(OpportunityStatus.ARCHIVED, OpportunityStatus.APPLYING));
        assertTrue(transitionService.canTransition(OpportunityStatus.ARCHIVED, OpportunityStatus.APPLIED));
    }

    @Test
    @DisplayName("Illegal transition throws InvalidLifecycleTransitionException")
    void illegalTransitionThrows() {
        // e.g., Cannot transition directly from SELECTED to REVIEWING or APPLYING without reopening
        assertFalse(transitionService.canTransition(OpportunityStatus.SELECTED, OpportunityStatus.APPLYING));
        assertThrows(InvalidLifecycleTransitionException.class, () ->
                transitionService.validateTransition(OpportunityStatus.SELECTED, OpportunityStatus.APPLYING));
    }
}
