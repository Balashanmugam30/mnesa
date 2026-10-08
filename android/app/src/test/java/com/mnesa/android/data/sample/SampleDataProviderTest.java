package com.mnesa.android.data.sample;

import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.Reminder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SampleDataProviderTest {

    @Test
    public void testSampleOpportunitiesDeterministic() {
        List<Opportunity> list = SampleDataProvider.getSampleOpportunities("user-test");

        assertNotNull(list);
        assertEquals(5, list.size());

        Opportunity hackathon = list.get(0);
        assertEquals("sample-opp-1", hackathon.getId());
        assertEquals("user-test", hackathon.getUserId());
        assertEquals("HACKATHON", hackathon.getCategory());
        assertTrue(hackathon.hasDeadline());

        Opportunity internship = list.get(1);
        assertEquals("sample-opp-2", internship.getId());
        assertEquals("Research Scientist Intern - Machine Learning", internship.getTitle());
        assertEquals("Google DeepMind", internship.getOrganization());
        assertEquals("HIGH", internship.getPriority());
    }

    @Test
    public void testSampleRemindersDeterministic() {
        List<Reminder> reminders = SampleDataProvider.getSampleReminders("user-test");

        assertNotNull(reminders);
        assertEquals(3, reminders.size());

        for (Reminder rem : reminders) {
            assertEquals("user-test", rem.getUserId());
            assertEquals("SCHEDULED", rem.getStatus());
            assertTrue(rem.getTriggerTimestamp() > 0);
        }
    }
}
