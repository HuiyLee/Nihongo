package com.example.japanese.controller;

import com.example.japanese.entity.Role;
import com.example.japanese.entity.StudyActivityType;
import com.example.japanese.entity.StudySession;
import com.example.japanese.entity.User;
import com.example.japanese.repository.RoleRepository;
import com.example.japanese.repository.StudySessionRepository;
import com.example.japanese.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirements sections 11, 16, 20, 22, 24, 35 (Phase 6). Covers: the
 * Reading translation-reveal gate, the spaced-repetition interval actually
 * growing/shrinking, the streak computed from seeded StudySession rows, the
 * notification fan-out firing only on a DRAFT->PUBLISHED transition and
 * staying scoped to the caller, and the progress/admin-stats endpoints
 * returning sane numbers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Phase6IntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudySessionRepository studySessionRepository;

    private String adminToken;
    private String userToken;
    private String username;
    private long levelId;
    private String testId;

    @BeforeEach
    void setUp() throws Exception {
        if (roleRepository.findByName(Role.USER).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.USER).description("Regular learner").build());
        }
        if (roleRepository.findByName(Role.ADMIN).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.ADMIN).description("Administrator").build());
        }

        // Same shared-context/DB caveat as ExamIntegrationTest - every unique-constrained
        // value must be scoped to this one test run.
        testId = Long.toString(System.nanoTime() % 100_000);
        adminToken = registerAndLogin("ph6_admin_" + testId, true);
        username = "ph6_user_" + testId;
        userToken = registerAndLogin(username, false);

        levelId = createLevel();
    }

    private String registerAndLogin(String username, boolean asAdmin) throws Exception {
        Map<String, String> registerBody = Map.of(
                "username", username,
                "email", username + "@example.com",
                "password", "Password1",
                "fullName", username
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerBody)))
                .andExpect(status().isCreated());

        if (asAdmin) {
            User user = userRepository.findByUsername(username).orElseThrow();
            Role adminRole = roleRepository.findByName(Role.ADMIN).orElseThrow();
            user.setRole(adminRole);
            userRepository.save(user);
        }

        Map<String, String> loginBody = Map.of("username", username, "password", "Password1");
        String responseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(responseJson).path("data").path("accessToken").asText();
    }

    private long createLevel() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "P6" + testId, "name", "Phase 6 Test Level", "description", "desc",
                "orderIndex", 80, "status", "PUBLISHED"
        );
        String responseJson = mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    private long createReading() throws Exception {
        Map<String, Object> body = Map.of(
                "levelId", levelId, "title", "Passage " + testId, "content", "本文",
                "translation", "secret-translation-" + testId, "difficulty", "EASY"
        );
        String responseJson = mockMvc.perform(post("/api/admin/readings")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    private long createVocabulary() throws Exception {
        Map<String, Object> body = Map.of(
                "levelId", levelId, "word", "学ぶ" + testId, "hiragana", "まなぶ",
                "romaji", "manabu", "meaning", "to study"
        );
        String responseJson = mockMvc.perform(post("/api/admin/vocabularies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    // ---- Reading translation reveal (section 16) ----

    @Test
    void reading_translationHiddenUntilCompleted_thenRevealed() throws Exception {
        long readingId = createReading();

        mockMvc.perform(get("/api/readings/" + readingId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed").value(false))
                .andExpect(jsonPath("$.data.translation").value(org.hamcrest.Matchers.nullValue()));

        mockMvc.perform(post("/api/readings/" + readingId + "/complete")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/readings/" + readingId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed").value(true))
                .andExpect(jsonPath("$.data.translation").value("secret-translation-" + testId));

        // Admins always see the translation, completion or not.
        mockMvc.perform(get("/api/admin/readings/" + readingId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.translation").value("secret-translation-" + testId));
    }

    // ---- Spaced repetition (section 11) ----

    @Test
    void spacedRepetition_intervalGrowsOnCorrect_thenShrinksOnIncorrect() throws Exception {
        long vocabularyId = createVocabulary();

        long firstGapDays = markAndGetIntervalDays(vocabularyId, "KNOWN");
        long secondGapDays = markAndGetIntervalDays(vocabularyId, "KNOWN");
        long thirdGapDays = markAndGetIntervalDays(vocabularyId, "UNKNOWN");

        Assertions.assertEquals(1, firstGapDays, "first correct review should schedule the initial 1-day interval");
        Assertions.assertEquals(2, secondGapDays, "a second consecutive correct review should double the interval");
        Assertions.assertTrue(thirdGapDays < secondGapDays, "an incorrect review should shrink the interval back down");
    }

    private long markAndGetIntervalDays(long vocabularyId, String outcome) throws Exception {
        String responseJson = mockMvc.perform(post("/api/vocabularies/" + vocabularyId + "/mark")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"outcome\":\"" + outcome + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode data = objectMapper.readTree(responseJson).path("data");
        LocalDateTime lastReviewedAt = LocalDateTime.parse(data.path("lastReviewedAt").asText());
        LocalDateTime nextReviewAt = LocalDateTime.parse(data.path("nextReviewAt").asText());
        return Duration.between(lastReviewedAt, nextReviewAt).toDays();
    }

    // ---- Streak (section 22) ----

    @Test
    void streak_countsConsecutiveDays_andTracksLongestSeparately() throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow();
        LocalDateTime today = LocalDateTime.now();

        seedStudySession(user, today.minusDays(2));
        seedStudySession(user, today.minusDays(1));
        seedStudySession(user, today);
        // An isolated day far in the past - contributes to neither the current nor a longer streak.
        seedStudySession(user, today.minusDays(20));

        mockMvc.perform(get("/api/streak")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentStreak").value(3))
                .andExpect(jsonPath("$.data.longestStreak").value(3));
    }

    private void seedStudySession(User user, LocalDateTime when) {
        StudySession session = StudySession.builder()
                .user(user)
                .startedAt(when)
                .endedAt(when)
                .durationSeconds(0)
                .activityType(StudyActivityType.VOCABULARY)
                .build();
        studySessionRepository.save(session);
    }

    // ---- Notifications (section 24) ----

    @Test
    void notification_firesOnlyOnPublishTransition_andIsScopedToCaller() throws Exception {
        long lessonId = createLesson("DRAFT");

        // Still a draft - no notification yet.
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0));

        updateLessonStatus(lessonId, "PUBLISHED");

        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1));

        mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].type").value("NEW_LESSON"))
                .andExpect(jsonPath("$.data.content[0].read").value(false));

        // Saving again while already PUBLISHED must not spam a second notification.
        updateLessonStatus(lessonId, "PUBLISHED");
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1));

        // A second user registered only now must not have been retroactively notified.
        String laterUserToken = registerAndLogin("ph6_late_" + testId, false);
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + laterUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0));

        // Marking read clears the count.
        String listJson = mockMvc.perform(get("/api/notifications")
                        .header("Authorization", "Bearer " + userToken))
                .andReturn().getResponse().getContentAsString();
        long notificationId = objectMapper.readTree(listJson).path("data").path("content").get(0).path("id").asLong();
        mockMvc.perform(post("/api/notifications/" + notificationId + "/read")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/notifications/unread-count")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(0));
    }

    private long createLesson(String status) throws Exception {
        Map<String, Object> body = Map.of(
                "levelId", levelId, "title", "Lesson " + testId, "description", "desc",
                "orderIndex", 1, "status", status
        );
        String responseJson = mockMvc.perform(post("/api/admin/lessons")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    private void updateLessonStatus(long lessonId, String status) throws Exception {
        Map<String, Object> body = Map.of(
                "levelId", levelId, "title", "Lesson " + testId, "description", "desc",
                "orderIndex", 1, "status", status
        );
        mockMvc.perform(put("/api/admin/lessons/" + lessonId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    // ---- Progress dashboard + admin stats (sections 20, 35) ----

    @Test
    void progress_reflectsKnownVocabulary_andAdminStatsReturnSaneTotals() throws Exception {
        long vocabularyId = createVocabulary();

        mockMvc.perform(post("/api/vocabularies/" + vocabularyId + "/mark")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"outcome\":\"KNOWN\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/progress/vocabulary")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.known").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.total").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.percent").value(org.hamcrest.Matchers.greaterThan(0)));

        mockMvc.perform(get("/api/progress")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.vocabulary.known").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.exams.percent").exists());

        mockMvc.perform(get("/api/admin/stats")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalUsers").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.totalVocabulary").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.passRate").value(org.hamcrest.Matchers.greaterThanOrEqualTo(0.0)));

        // A non-admin must not reach the admin stats endpoint.
        mockMvc.perform(get("/api/admin/stats")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
