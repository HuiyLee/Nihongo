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

/**
 * Requirements section 14. Covers admin-only write access, that the public
 * response never carries isCorrect (the whole grading mechanism depends on
 * that), and the submit-grading rules for single- and multi-answer types.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExerciseIntegrationTest {

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
    private long levelId;

    @BeforeEach
    void setUp() throws Exception {
        if (roleRepository.findByName(Role.USER).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.USER).description("Regular learner").build());
        }
        if (roleRepository.findByName(Role.ADMIN).isEmpty()) {
            roleRepository.save(Role.builder().name(Role.ADMIN).description("Administrator").build());
        }

        adminToken = registerAndLogin("exercise_admin", true);
        userToken = registerAndLogin("exercise_user", false);

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
                "code", "EX5", "name", "Exercise Test Level", "description", "desc",
                "orderIndex", 60, "status", "PUBLISHED"
        );
        String responseJson = mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    private String multipleChoiceBody() {
        return """
                {
                  "levelId": %d,
                  "type": "MULTIPLE_CHOICE",
                  "question": "What does 食べる mean?",
                  "explanation": "食べる means 'to eat'.",
                  "difficulty": "EASY",
                  "answers": [
                    {"answerText": "to eat", "correct": true, "orderIndex": 1},
                    {"answerText": "to drink", "correct": false, "orderIndex": 2},
                    {"answerText": "to sleep", "correct": false, "orderIndex": 3}
                  ]
                }
                """.formatted(levelId);
    }

    private JsonNode createExercise(String body) throws Exception {
        String responseJson = mockMvc.perform(post("/api/admin/exercises")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data");
    }

    @Test
    void nonAdmin_cannotCreateExercise() throws Exception {
        mockMvc.perform(post("/api/admin/exercises")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(multipleChoiceBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminResponse_includesIsCorrect_publicResponse_neverDoes() throws Exception {
        JsonNode created = createExercise(multipleChoiceBody());
        long exerciseId = created.path("id").asLong();

        // Admin detail view: every answer carries "correct".
        mockMvc.perform(get("/api/admin/exercises/" + exerciseId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answers[0].correct").exists());

        // Public/learner detail view: the field must not be present at all.
        mockMvc.perform(get("/api/exercises/" + exerciseId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answers[0].correct").doesNotExist())
                .andExpect(jsonPath("$.data.answers[0].isCorrect").doesNotExist())
                .andExpect(jsonPath("$.data.answers[0].answerText").exists());
    }

    @Test
    void submit_multipleChoice_correctAndIncorrect() throws Exception {
        JsonNode created = createExercise(multipleChoiceBody());
        long exerciseId = created.path("id").asLong();

        long correctAnswerId = -1;
        long wrongAnswerId = -1;
        for (JsonNode answer : created.path("answers")) {
            if (answer.path("correct").asBoolean()) {
                correctAnswerId = answer.path("id").asLong();
            } else if (wrongAnswerId == -1) {
                wrongAnswerId = answer.path("id").asLong();
            }
        }

        mockMvc.perform(post("/api/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answerIds\":[" + correctAnswerId + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correct").value(true))
                .andExpect(jsonPath("$.data.score").value(1));

        mockMvc.perform(post("/api/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answerIds\":[" + wrongAnswerId + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correct").value(false))
                .andExpect(jsonPath("$.data.score").value(0));
    }

    @Test
    void submit_multipleAnswer_requiresExactCorrectSet() throws Exception {
        String body = """
                {
                  "levelId": %d,
                  "type": "MULTIPLE_ANSWER",
                  "question": "Which of these are true?",
                  "difficulty": "MEDIUM",
                  "answers": [
                    {"answerText": "A", "correct": true, "orderIndex": 1},
                    {"answerText": "B", "correct": true, "orderIndex": 2},
                    {"answerText": "C", "correct": false, "orderIndex": 3}
                  ]
                }
                """.formatted(levelId);
        JsonNode created = createExercise(body);
        long exerciseId = created.path("id").asLong();

        long idA = -1;
        long idB = -1;
        long idC = -1;
        for (JsonNode answer : created.path("answers")) {
            switch (answer.path("answerText").asText()) {
                case "A" -> idA = answer.path("id").asLong();
                case "B" -> idB = answer.path("id").asLong();
                case "C" -> idC = answer.path("id").asLong();
                default -> { }
            }
        }

        // Only A selected (missing B) - not correct.
        mockMvc.perform(post("/api/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answerIds\":[" + idA + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correct").value(false));

        // A and B selected, C not - exact match, correct.
        mockMvc.perform(post("/api/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answerIds\":[" + idA + "," + idB + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correct").value(true));

        // A, B, and the wrong C selected - not correct.
        mockMvc.perform(post("/api/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answerIds\":[" + idA + "," + idB + "," + idC + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correct").value(false));
    }

    @Test
    void submit_withAnswerIdFromAnotherExercise_isRejected() throws Exception {
        JsonNode first = createExercise(multipleChoiceBody());
        JsonNode second = createExercise(multipleChoiceBody());
        long secondExerciseId = second.path("id").asLong();
        long answerFromFirstExercise = first.path("answers").get(0).path("id").asLong();

        mockMvc.perform(post("/api/exercises/" + secondExerciseId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answerIds\":[" + answerFromFirstExercise + "]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submit_withEmptyAnswerIds_isRejectedByValidation() throws Exception {
        JsonNode created = createExercise(multipleChoiceBody());
        long exerciseId = created.path("id").asLong();

        mockMvc.perform(post("/api/exercises/" + exerciseId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answerIds\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void creatingExercise_withNoAnswers_isRejectedByValidation() throws Exception {
        String body = """
                {
                  "levelId": %d,
                  "type": "TRUE_FALSE",
                  "question": "Some question",
                  "difficulty": "EASY",
                  "answers": []
                }
                """.formatted(levelId);

        mockMvc.perform(post("/api/admin/exercises")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
