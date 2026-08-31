package com.example.japanese.controller;

import com.example.japanese.entity.Role;
import com.example.japanese.entity.User;
import com.example.japanese.repository.RoleRepository;
import com.example.japanese.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirements section 33 (a user's learning data belongs only to them) and
 * BR-011 (no duplicate bookmarks). Covers Phase 3: per-user learning state
 * on Vocabulary, Bookmarks, and Study Sessions.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserLearningIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    private String adminToken;
    private String userAToken;
    private String userBToken;
    private long vocabularyId;
    private long kanjiId;

    @BeforeEach
    void setUp() throws Exception {
        if (roleRepository.findByName(Role.USER).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.USER).description("Regular learner").build());
        }
        if (roleRepository.findByName(Role.ADMIN).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.ADMIN).description("Administrator").build());
        }

        adminToken = registerAndLogin("learning_admin", true);
        userAToken = registerAndLogin("learning_user_a", false);
        userBToken = registerAndLogin("learning_user_b", false);

        long levelId = createLevel();
        vocabularyId = createVocabulary(levelId);
        kanjiId = createKanji(levelId);
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
                "code", "LU5", "name", "Learning Test Level", "description", "desc",
                "orderIndex", 50, "status", "PUBLISHED"
        );
        String responseJson = mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    private long createVocabulary(long levelId) throws Exception {
        Map<String, Object> body = Map.of(
                "levelId", levelId, "word", "学ぶ", "hiragana", "まなぶ",
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

    private long createKanji(long levelId) throws Exception {
        Map<String, Object> body = Map.of(
                "levelId", levelId, "character", "学", "meaning", "study", "onyomi", "ガク", "kunyomi", "まな.ぶ"
        );
        String responseJson = mockMvc.perform(post("/api/admin/kanji")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    @Test
    void markingVocabularyKnown_isIsolatedPerUser() throws Exception {
        mockMvc.perform(post("/api/vocabularies/" + vocabularyId + "/mark")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content("{\"outcome\":\"KNOWN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("KNOWN"))
                .andExpect(jsonPath("$.data.correctCount").value(1));

        // User A now sees KNOWN with correctCount 1.
        mockMvc.perform(get("/api/vocabularies/" + vocabularyId + "/progress")
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("KNOWN"))
                .andExpect(jsonPath("$.data.correctCount").value(1));

        // User B's progress on the exact same vocabulary is untouched - still the NEW default.
        mockMvc.perform(get("/api/vocabularies/" + vocabularyId + "/progress")
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("NEW"))
                .andExpect(jsonPath("$.data.correctCount").value(0));
    }

    @Test
    void markingUnknown_incrementsWrongCount_andSetsLearningStatus() throws Exception {
        mockMvc.perform(post("/api/kanji/" + kanjiId + "/mark")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content("{\"outcome\":\"UNKNOWN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("LEARNING"))
                .andExpect(jsonPath("$.data.wrongCount").value(1));
    }

    @Test
    void bookmark_duplicateForSameTarget_returnsConflict() throws Exception {
        Map<String, Object> body = Map.of("targetType", "VOCABULARY", "targetId", vocabularyId);

        mockMvc.perform(post("/api/bookmarks")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.displayText").value("学ぶ"));

        mockMvc.perform(post("/api/bookmarks")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    void bookmark_forNonExistentTarget_returnsNotFound() throws Exception {
        Map<String, Object> body = Map.of("targetType", "VOCABULARY", "targetId", 999999L);

        mockMvc.perform(post("/api/bookmarks")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookmark_targetTypeReading_isRejectedAsNotYetSupported() throws Exception {
        Map<String, Object> body = Map.of("targetType", "READING", "targetId", 1L);

        mockMvc.perform(post("/api/bookmarks")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookmarks_areIsolatedPerUser_andOnlyOwnerCanDelete() throws Exception {
        Map<String, Object> body = Map.of("targetType", "KANJI", "targetId", kanjiId);
        String responseJson = mockMvc.perform(post("/api/bookmarks")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long bookmarkId = objectMapper.readTree(responseJson).path("data").path("id").asLong();

        // User B's own bookmark list must not contain user A's bookmark.
        mockMvc.perform(get("/api/bookmarks").header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[?(@.id == " + bookmarkId + ")]").isEmpty());

        // User B cannot delete user A's bookmark - it must not be found in B's own scope.
        mockMvc.perform(delete("/api/bookmarks/" + bookmarkId)
                        .header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isNotFound());

        // The owner can see and delete it.
        mockMvc.perform(get("/api/bookmarks").header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[?(@.id == " + bookmarkId + ")]").isNotEmpty());

        mockMvc.perform(delete("/api/bookmarks/" + bookmarkId)
                        .header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk());
    }

    @Test
    void studySessions_areIsolatedPerUser() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusMinutes(10);
        LocalDateTime end = LocalDateTime.now();
        Map<String, Object> body = Map.of(
                "activityType", "VOCABULARY",
                "referenceId", vocabularyId,
                "startedAt", start.toString(),
                "endedAt", end.toString()
        );

        mockMvc.perform(post("/api/study-sessions")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.durationSeconds").value(600));

        mockMvc.perform(get("/api/study-sessions").header("Authorization", "Bearer " + userAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1));

        // User B has recorded nothing - must see an empty list, not user A's session.
        mockMvc.perform(get("/api/study-sessions").header("Authorization", "Bearer " + userBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void studySession_endedBeforeStarted_isRejected() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusMinutes(5);
        Map<String, Object> body = Map.of(
                "activityType", "KANJI",
                "startedAt", start.toString(),
                "endedAt", end.toString()
        );

        mockMvc.perform(post("/api/study-sessions")
                        .header("Authorization", "Bearer " + userAToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}
