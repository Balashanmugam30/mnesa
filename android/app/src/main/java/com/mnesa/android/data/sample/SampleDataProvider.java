package com.mnesa.android.data.sample;

import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.model.Reminder;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides deterministic local sample opportunities and reminders for development and demo mode.
 */
public final class SampleDataProvider {

    private SampleDataProvider() {
    }

    public static List<Opportunity> getSampleOpportunities(String userId) {
        long now = System.currentTimeMillis();
        long dayMs = 24L * 60 * 60 * 1000;

        List<Opportunity> list = new ArrayList<>();

        // 1. Urgent Hackathon - 3 days left
        list.add(new Opportunity(
                "sample-opp-1",
                userId,
                "Global Generative AI Hackathon 2026",
                "Anthropic & AWS",
                OpportunityType.HACKATHON,
                "HACKATHON",
                OpportunityStatus.CAPTURED,
                "48-hour global virtual hackathon building agentic workflows and multimodal applications.",
                "https://example.com/hackathon/genai2026",
                "https://example.com/register/genai2026",
                now + (3 * dayMs),
                "UTC",
                "Open to all developers and students worldwide",
                "Virtual / Global",
                "Weekend project",
                "HIGH",
                0.98f,
                now - (2 * dayMs),
                now - (2 * dayMs),
                "SYNCED"
        ));

        // 2. High-Priority AI/ML Internship - 7 days left
        list.add(new Opportunity(
                "sample-opp-2",
                userId,
                "Research Scientist Intern - Machine Learning",
                "Google DeepMind",
                OpportunityType.INTERNSHIP,
                "INTERNSHIP",
                OpportunityStatus.UNDERSTOOD,
                "3-month paid summer research internship focusing on reinforcement learning and foundation models.",
                "https://example.com/careers/deepmind-intern-2026",
                "https://example.com/apply/deepmind-intern-2026",
                now + (7 * dayMs),
                "America/Los_Angeles",
                "PhD or Master's students in Computer Science, Math, or related STEM fields",
                "San Francisco, CA / London, UK",
                "15-20 hours for application prep",
                "HIGH",
                0.95f,
                now - (4 * dayMs),
                now - (1 * dayMs),
                "SYNCED"
        ));

        // 3. Cloud Engineering Internship - 14 days left
        list.add(new Opportunity(
                "sample-opp-3",
                userId,
                "Infrastructure & Cloud Systems Engineer Intern",
                "Stripe",
                OpportunityType.INTERNSHIP,
                "INTERNSHIP",
                OpportunityStatus.CAPTURED,
                "Summer internship designing fault-tolerant financial infrastructure and distributed systems.",
                "https://example.com/stripe/jobs/cloud-systems",
                "https://example.com/stripe/apply/cloud-systems",
                now + (14 * dayMs),
                "America/New_York",
                "Undergraduate students graduating in 2027",
                "Seattle, WA / Remote",
                "Standard engineering resume & portfolio",
                "MEDIUM",
                0.92f,
                now - (5 * dayMs),
                now - (5 * dayMs),
                "SYNCED"
        ));

        // 4. STEM Scholarship - 30 days left
        list.add(new Opportunity(
                "sample-opp-4",
                userId,
                "Future Leaders in Technology Scholarship",
                "Generation Google Scholarship Foundation",
                OpportunityType.SCHOLARSHIP,
                "SCHOLARSHIP",
                OpportunityStatus.CAPTURED,
                "$10,000 tuition grant awarded to students demonstrating excellence in computer science.",
                "https://example.com/scholarships/generation-google",
                "https://example.com/scholarships/apply",
                now + (30 * dayMs),
                "UTC",
                "Enrolled undergraduate students in Computer Science",
                "Global",
                "Two essay prompts and one recommendation letter",
                "MEDIUM",
                0.94f,
                now - (6 * dayMs),
                now - (6 * dayMs),
                "SYNCED"
        ));

        // 5. Developer Conference - 60 days left
        list.add(new Opportunity(
                "sample-opp-5",
                userId,
                "KubeCon + CloudNativeCon North America 2026",
                "Cloud Native Computing Foundation",
                OpportunityType.CONFERENCE,
                "CONFERENCE",
                OpportunityStatus.SAVED,
                "Premier conference bringing together leading adopters and technologists in cloud-native computing.",
                "https://example.com/events/kubecon2026",
                "https://example.com/tickets/kubecon2026",
                now + (60 * dayMs),
                "America/Chicago",
                "Developers, architects, students",
                "Chicago, IL",
                "Student scholarship passes available",
                "LOW",
                0.90f,
                now - (10 * dayMs),
                now - (10 * dayMs),
                "SYNCED"
        ));

        return list;
    }

    public static List<Reminder> getSampleReminders(String userId) {
        long now = System.currentTimeMillis();
        long dayMs = 24L * 60 * 60 * 1000;

        List<Reminder> reminders = new ArrayList<>();

        reminders.add(new Reminder(
                "sample-rem-1",
                "sample-opp-1",
                userId,
                "Submit project proposal & team registration for GenAI Hackathon",
                now + (1 * dayMs),
                "STANDARD",
                "SCHEDULED",
                now
        ));

        reminders.add(new Reminder(
                "sample-rem-2",
                "sample-opp-2",
                userId,
                "Finalize research statement for Google DeepMind Internship",
                now + (4 * dayMs),
                "AGGRESSIVE",
                "SCHEDULED",
                now
        ));

        reminders.add(new Reminder(
                "sample-rem-3",
                "sample-opp-4",
                userId,
                "Request recommendation letters for Technology Scholarship",
                now + (10 * dayMs),
                "STANDARD",
                "SCHEDULED",
                now
        ));

        return reminders;
    }
}
