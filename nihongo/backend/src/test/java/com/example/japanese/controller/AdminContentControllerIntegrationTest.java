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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminContentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    private String adminToken;
    private String userToken;
    private Long levelId;

    @BeforeEach
    void setUp() throws Exception {
        if (roleRepository.findByName(Role.USER).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.USER).description("Regular learner").build());
        }
        if (roleRepository.findByName(Role.ADMIN).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.ADMIN).description("Administrator").build());
        }

        adminToken = registerAndLogin("content_admin", true);
        userToken = registerAndLogin("content_user", false);

        levelId = createLevel("N5", "JLPT N5", 1);
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

    private Long createLevel(String code, String name, int orderIndex) throws Exception {
        Map<String, Object> body = Map.of(
                "code", code, "name", name, "description", "desc",
                "orderIndex", orderIndex, "status", "PUBLISHED"
        );
        String responseJson = mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode data = objectMapper.readTree(responseJson).path("data");
        return data.path("id").asLong();
    }

    @Test
    void nonAdmin_cannotCreateLevel() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "N4", "name", "JLPT N4", "orderIndex", 2, "status", "PUBLISHED"
        );
        mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }

    @Test
    void creatingLevel_withDuplicateCode_returnsConflict() throws Exception {
        Map<String, Object> body = Map.of(
                "code", "N5", "name", "Duplicate N5", "orderIndex", 99, "status", "PUBLISHED"
        );
        mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    void draftLesson_isHiddenFromRegularUser_butVisibleToAdmin() throws Exception {
        Map<String, Object> lessonBody = Map.of(
                "levelId", levelId, "title", "Draft lesson", "orderIndex", 1, "status", "DRAFT"
        );
        String responseJson = mockMvc.perform(post("/api/admin/lessons")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(lessonBody)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long lessonId = objectMapper.readTree(responseJson).path("data").path("id").asLong();

        // Regular user hits the public endpoint: draft lesson must not appear, and direct GET by id is 404.
        mockMvc.perform(get("/api/lessons").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[?(@.id == " + lessonId + ")]").isEmpty());

        mockMvc.perform(get("/api/lessons/" + lessonId).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());

        // Admin hitting the same public endpoint still sees it (preview capability).
        mockMvc.perform(get("/api/lessons/" + lessonId).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Draft lesson"));
    }

    @Test
    void vocabularySearch_matchesByKeywordAcrossFields() throws Exception {
        Map<String, Object> vocabBody = Map.of(
                "levelId", levelId,
                "word", "食べる",
                "hiragana", "たべる",
                "romaji", "taberu",
                "meaning", "to eat"
        );
        mockMvc.perform(post("/api/admin/vocabularies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(vocabBody)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/vocabularies")
                        .header("Authorization", "Bearer " + userToken)
                        .param("keyword", "taberu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].meaning").value("to eat"));

        mockMvc.perform(get("/api/vocabularies")
                        .header("Authorization", "Bearer " + userToken)
                        .param("keyword", "no-such-word"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }
}
