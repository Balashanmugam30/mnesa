package com.mnesa.android.domain.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class OpportunityTest {

    @Test
    public void testOpportunityCreationAndDefaults() {
        Opportunity opportunity = new Opportunity(
                "opp-1",
                "Google Summer of Code 2026",
                "Google",
                OpportunityType.INTERNSHIP,
                OpportunityStatus.SAVED,
                "https://summerofcode.withgoogle.com",
                1776240000000L,
                0.95f,
                1770000000000L
        );

        assertEquals("opp-1", opportunity.getId());
        assertEquals("Google Summer of Code 2026", opportunity.getTitle());
        assertEquals("Google", opportunity.getOrganization());
        assertEquals(OpportunityType.INTERNSHIP, opportunity.getType());
        assertEquals(OpportunityStatus.SAVED, opportunity.getStatus());
        assertTrue(opportunity.hasDeadline());
        assertEquals(0.95f, opportunity.getConfidenceScore(), 0.001f);
    }

    @Test
    public void testOpportunityEqualityBasedOnId() {
        Opportunity opp1 = new Opportunity("same-id", "Title A", "Org A", OpportunityType.JOB, OpportunityStatus.CAPTURED, null, null, 0.5f, 1000L);
        Opportunity opp2 = new Opportunity("same-id", "Title B", "Org B", OpportunityType.EVENT, OpportunityStatus.SAVED, null, null, 0.8f, 2000L);

        assertEquals(opp1, opp2);
        assertEquals(opp1.hashCode(), opp2.hashCode());
    }
}
