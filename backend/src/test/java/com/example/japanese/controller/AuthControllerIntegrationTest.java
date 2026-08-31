package com.example.japanese.controller;

import com.example.japanese.entity.Role;
import com.example.japanese.repository.RoleRepository;
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
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void seedRoles() {
        if (roleRepository.findByName(Role.USER).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.USER).description("Regular learner").build());
        }
        if (roleRepository.findByName(Role.ADMIN).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.ADMIN).description("Administrator").build());
        }
    }

    @Test
    void register_thenLogin_returnsTokens() throws Exception {
        Map<String, String> registerBody = Map.of(
                "username", "jlpt_user",
                "email", "jlpt_user@example.com",
                "password", "Password1",
                "fullName", "JLPT User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.username").value("jlpt_user"));

        Map<String, String> loginBody = Map.of("username", "jlpt_user", "password", "Password1");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.role").value(Role.USER));
    }

    @Test
    void register_withDuplicateUsername_returnsConflict() throws Exception {
        Map<String, String> registerBody = Map.of(
                "username", "dup_user",
                "email", "dup_user@example.com",
                "password", "Password1",
                "fullName", "Dup User"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerBody)))
                .andExpect(status().isCreated());

        Map<String, String> secondBody = Map.of(
                "username", "dup_user",
                "email", "other@example.com",
                "password", "Password1",
                "fullName", "Dup User 2"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(secondBody)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    void login_withInvalidCredentials_returnsUnauthorized() throws Exception {
        Map<String, String> loginBody = Map.of("username", "no_such_user", "password", "whatever1A");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("ERROR"));
    }

    @Test
    void protectedEndpoint_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_withUserToken_returnsForbidden() throws Exception {
        Map<String, String> registerBody = Map.of(
                "username", "plain_user",
                "email", "plain_user@example.com",
                "password", "Password1",
                "fullName", "Plain User"
        );
        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(registerBody)))
                .andExpect(status().isCreated());

        Map<String, String> loginBody = Map.of("username", "plain_user", "password", "Password1");
        String responseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String accessToken = objectMapper.readTree(responseJson).path("data").path("accessToken").asText();

        mockMvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }
}
