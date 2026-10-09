package com.mnesa.backend.modules.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.attachment.domain.Attachment;
import com.mnesa.backend.modules.attachment.repository.AttachmentRepository;
import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.opportunity.dto.UpdateStatusRequest;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import com.mnesa.backend.modules.reminder.domain.Reminder;
import com.mnesa.backend.modules.reminder.domain.ReminderStatus;
import com.mnesa.backend.modules.reminder.domain.ReminderType;
import com.mnesa.backend.modules.reminder.dto.SnoozeReminderRequest;
import com.mnesa.backend.modules.reminder.repository.ReminderRepository;
import com.mnesa.backend.modules.user.domain.User;
import com.mnesa.backend.modules.user.domain.UserRole;
import com.mnesa.backend.modules.user.domain.UserStatus;
import com.mnesa.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests verifying tenant isolation, cross-account IDOR protection,
 * and cascading account deletion across database boundaries using disposable test accounts.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CrossAccountIsolationAndIdorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    private User userA;
    private User userB;
    private Opportunity opportunityB;
    private Reminder reminderB;
    private Attachment attachmentB;

    @BeforeEach
    void setUp() {
        // Create isolated User A
        userA = userRepository.save(User.builder()
                .email("alice_tenant_a@test.local")
                .fullName("Alice Tenant")
                .passwordHash("argon2id$placeholder")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());

        // Create isolated User B
        userB = userRepository.save(User.builder()
                .email("bob_tenant_b@test.local")
                .fullName("Bob Tenant")
                .passwordHash("argon2id$placeholder")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());

        // Create private Opportunity owned by User B
        opportunityB = opportunityRepository.save(Opportunity.builder()
                .userId(userB.getId())
                .title("Bob Confidential Fellowship")
                .organization("Confidential Labs")
                .opportunityType(OpportunityType.SCHOLARSHIP)
                .status(OpportunityStatus.SAVED)
                .deadlineAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build());

        // Create private Reminder owned by User B
        reminderB = reminderRepository.save(Reminder.builder()
                .user(userB)
                .opportunity(opportunityB)
                .title("Submit Fellowship Proposal")
                .reminderType(ReminderType.APPROACHING_DEADLINE)
                .scheduledAt(Instant.now().plus(3, ChronoUnit.DAYS))
                .status(ReminderStatus.SCHEDULED)
                .build());

        // Create private Attachment owned by User B
        attachmentB = attachmentRepository.save(Attachment.builder()
                .userId(userB.getId())
                .opportunityId(opportunityB.getId())
                .fileName("confidential_screenshot.png")
                .storagePath("inline_base64")
                .mimeType("image/png")
                .fileSizeBytes(1024L)
                .build());
    }

    @Test
    @DisplayName("IDOR Prevention: User A cannot read User B's opportunity by ID")
    void userACannotReadUserBOpportunity() throws Exception {
        UserPrincipal principalA = UserPrincipal.create(userA);

        mockMvc.perform(get("/api/v1/opportunities/" + opportunityB.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(principalA)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("IDOR Prevention: User A cannot update User B's opportunity status")
    void userACannotUpdateUserBOpportunityStatus() throws Exception {
        UserPrincipal principalA = UserPrincipal.create(userA);
        UpdateStatusRequest request = new UpdateStatusRequest("APPLIED", "Tampered note by User A");

        mockMvc.perform(patch("/api/v1/opportunities/" + opportunityB.getId() + "/status")
                        .with(SecurityMockMvcRequestPostProcessors.user(principalA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Verify Bob's opportunity was NOT modified in the database
        Opportunity untouched = opportunityRepository.findById(opportunityB.getId()).orElseThrow();
        assertThat(untouched.getStatus()).isEqualTo(OpportunityStatus.SAVED);
    }

    @Test
    @DisplayName("IDOR Prevention: User A cannot archive User B's opportunity")
    void userACannotArchiveUserBOpportunity() throws Exception {
        UserPrincipal principalA = UserPrincipal.create(userA);

        mockMvc.perform(post("/api/v1/opportunities/" + opportunityB.getId() + "/archive")
                        .with(SecurityMockMvcRequestPostProcessors.user(principalA)))
                .andExpect(status().isNotFound());

        Opportunity untouched = opportunityRepository.findById(opportunityB.getId()).orElseThrow();
        assertThat(untouched.getStatus()).isEqualTo(OpportunityStatus.SAVED);
    }

    @Test
    @DisplayName("IDOR Prevention: User A cannot delete User B's opportunity")
    void userACannotDeleteUserBOpportunity() throws Exception {
        UserPrincipal principalA = UserPrincipal.create(userA);

        mockMvc.perform(delete("/api/v1/opportunities/" + opportunityB.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(principalA)))
                .andExpect(status().isNotFound());

        // Verify Bob's opportunity still exists
        assertThat(opportunityRepository.findById(opportunityB.getId())).isPresent();
    }

    @Test
    @DisplayName("Tenant Isolation: User A opportunity search returns 0 results when User B has opportunities")
    void userASearchDoesNotLeakUserBOpportunities() throws Exception {
        UserPrincipal principalA = UserPrincipal.create(userA);

        mockMvc.perform(get("/api/v1/opportunities")
                        .with(SecurityMockMvcRequestPostProcessors.user(principalA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("IDOR Prevention: User A cannot snooze User B's reminder")
    void userACannotSnoozeUserBReminder() throws Exception {
        UserPrincipal principalA = UserPrincipal.create(userA);
        SnoozeReminderRequest request = SnoozeReminderRequest.builder()
                .snoozeDurationMinutes(60)
                .build();

        mockMvc.perform(post("/api/v1/reminders/" + reminderB.getId() + "/snooze")
                        .with(SecurityMockMvcRequestPostProcessors.user(principalA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        // Verify Bob's reminder remains SCHEDULED
        Reminder untouched = reminderRepository.findById(reminderB.getId()).orElseThrow();
        assertThat(untouched.getStatus()).isEqualTo(ReminderStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Tenant Isolation: User A reminder list does not expose User B's reminders")
    void userAReminderListDoesNotLeakUserBReminders() throws Exception {
        UserPrincipal principalA = UserPrincipal.create(userA);

        mockMvc.perform(get("/api/v1/reminders")
                        .with(SecurityMockMvcRequestPostProcessors.user(principalA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    @DisplayName("Account Deletion: User B deletion cascades and removes B's records while leaving A intact")
    void accountDeletionCascadesCleanlyWithoutImpactingOtherUsers() throws Exception {
        UserPrincipal principalB = UserPrincipal.create(userB);

        mockMvc.perform(delete("/api/v1/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principalB)))
                .andExpect(status().isNoContent());

        // Verify User B and all B-owned entities are deleted
        assertThat(userRepository.findById(userB.getId())).isEmpty();
        assertThat(opportunityRepository.findById(opportunityB.getId())).isEmpty();
        assertThat(reminderRepository.findById(reminderB.getId())).isEmpty();
        assertThat(attachmentRepository.findAllByUserId(userB.getId())).isEmpty();

        // Verify User A remains completely untouched and intact
        assertThat(userRepository.findById(userA.getId())).isPresent();
    }
}
