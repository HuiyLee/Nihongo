package com.example.japanese.controller;

import com.example.japanese.entity.Role;
import com.example.japanese.entity.User;
import com.example.japanese.repository.RoleRepository;
import com.example.japanese.repository.UserRepository;
import com.example.japanese.service.ai.AnthropicClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirements section 38, Phase 7. AnthropicClient is mocked so these tests
 * never hit the real Anthropic API (and pass with no ANTHROPIC_API_KEY
 * configured); AnthropicClientTest separately covers the real "not
 * configured" 503 path. Covers auth, request validation, and that AiService's
 * parsing/lookup logic is wired correctly end to end through the controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private AnthropicClient anthropicClient;

    private final String testId = Long.toString(System.nanoTime() % 100_000);

    private String userToken;
    private String adminToken;
    private long levelId;
    private long grammarId;

    @BeforeEach
    void setUp() throws Exception {
        if (roleRepository.findByName(Role.USER).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.USER).description("Regular learner").build());
        }
        if (roleRepository.findByName(Role.ADMIN).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.ADMIN).description("Administrator").build());
        }

        userToken = registerAndLogin("ai_user_" + testId, false);
        adminToken = registerAndLogin("ai_admin_" + testId, true);
        levelId = createLevel();
        grammarId = createGrammar(levelId);
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
                "code", "AI" + testId, "name", "AI Test Level", "description", "desc",
                "orderIndex", 90, "status", "PUBLISHED"
        );
        String responseJson = mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    private long createGrammar(long levelId) throws Exception {
        Map<String, Object> body = Map.of(
                "levelId", levelId,
                "pattern", "〜ばかりだ",
                "meaning", "keeps on doing/being ~"
        );
        String responseJson = mockMvc.perform(post("/api/admin/grammars")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    @Test
    void writingCorrection_withoutAuth_isRejected() throws Exception {
        mockMvc.perform(post("/api/ai/writing-correction")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("text", "こんにちは"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void writingCorrection_withBlankText_isRejected() throws Exception {
        mockMvc.perform(post("/api/ai/writing-correction")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("text", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void writingCorrection_happyPath_parsesCorrectedAndFeedback() throws Exception {
        when(anthropicClient.complete(anyString(), any())).thenReturn(
                "###CORRECTED###\n私は学生です。\n###FEEDBACK###\nGood - just a missing particle."
        );

        mockMvc.perform(post("/api/ai/writing-correction")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("text", "わたし がくせいです"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.corrected").value("私は学生です。"))
                .andExpect(jsonPath("$.data.feedback").value("Good - just a missing particle."));
    }

    @Test
    void grammarExplanation_withNeitherGrammarIdNorQuestion_isRejected() throws Exception {
        mockMvc.perform(post("/api/ai/grammar-explanation")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void grammarExplanation_withUnknownGrammarId_returnsNotFound() throws Exception {
        mockMvc.perform(post("/api/ai/grammar-explanation")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("grammarId", 9_999_999))))
                .andExpect(status().isNotFound());
    }

    @Test
    void grammarExplanation_withKnownGrammarId_returnsExplanationAndPattern() throws Exception {
        when(anthropicClient.complete(anyString(), any())).thenReturn("Detailed explanation here.");

        mockMvc.perform(post("/api/ai/grammar-explanation")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("grammarId", grammarId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pattern").value("〜ばかりだ"))
                .andExpect(jsonPath("$.data.explanation").value("Detailed explanation here."));
    }

    @Test
    void conversation_withEmptyMessages_isRejected() throws Exception {
        mockMvc.perform(post("/api/ai/conversation")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("messages", List.of()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void conversation_happyPath_returnsReply() throws Exception {
        when(anthropicClient.complete(anyString(), any())).thenReturn("こんにちは!元気ですか?");

        Map<String, Object> body = Map.of(
                "level", "N5",
                "messages", List.of(Map.of("role", "user", "content", "こんにちは"))
        );
        mockMvc.perform(post("/api/ai/conversation")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reply").value("こんにちは!元気ですか?"));
    }
}
