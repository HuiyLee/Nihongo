package com.example.japanese.controller;

import com.example.japanese.entity.ExamAttempt;
import com.example.japanese.entity.ExamAttemptStatus;
import com.example.japanese.entity.Role;
import com.example.japanese.entity.User;
import com.example.japanese.repository.ExamAttemptRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Requirements section 17-19. Covers admin-only write access, the
 * BR-006/007/008 draft-visibility split reused from Lesson, that the
 * backend (not the client) is the sole source of truth for the exam
 * clock (BR-009) and the score (BR-010), and idempotent start/submit.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExamIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExamAttemptRepository examAttemptRepository;

    private String adminToken;
    private String userToken;
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

        // Each @Test method reuses the same shared Spring context/DB (no rollback
        // between tests), so every unique-constrained value (username, level code)
        // must be scoped to this one test run rather than hardcoded per class.
        testId = Long.toString(System.nanoTime() % 100_000);
        adminToken = registerAndLogin("exam_admin_" + testId, true);
        userToken = registerAndLogin("exam_user_" + testId, false);

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
                "code", "EXM" + testId, "name", "Exam Test Level", "description", "desc",
                "orderIndex", 70, "status", "PUBLISHED"
        );
        String responseJson = mockMvc.perform(post("/api/admin/levels")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data").path("id").asLong();
    }

    /** Creates a MULTIPLE_CHOICE exercise with one correct and two wrong answers, returns the created admin JSON. */
    private JsonNode createExercise(String question) throws Exception {
        String body = """
                {
                  "levelId": %d,
                  "type": "MULTIPLE_CHOICE",
                  "question": "%s",
                  "difficulty": "EASY",
                  "answers": [
                    {"answerText": "correct", "correct": true, "orderIndex": 1},
                    {"answerText": "wrong1", "correct": false, "orderIndex": 2},
                    {"answerText": "wrong2", "correct": false, "orderIndex": 3}
                  ]
                }
                """.formatted(levelId, question);
        String responseJson = mockMvc.perform(post("/api/admin/exercises")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data");
    }

    private long correctAnswerId(JsonNode exercise) {
        for (JsonNode answer : exercise.path("answers")) {
            if (answer.path("correct").asBoolean()) {
                return answer.path("id").asLong();
            }
        }
        throw new IllegalStateException("no correct answer found");
    }

    private long wrongAnswerId(JsonNode exercise) {
        for (JsonNode answer : exercise.path("answers")) {
            if (!answer.path("correct").asBoolean()) {
                return answer.path("id").asLong();
            }
        }
        throw new IllegalStateException("no wrong answer found");
    }

    private JsonNode createExam(String status, int durationMinutes, long exercise1Id, long exercise2Id) throws Exception {
        String body = """
                {
                  "levelId": %d,
                  "title": "Test Exam",
                  "description": "desc",
                  "durationMinutes": %d,
                  "status": "%s",
                  "questions": [
                    {"exerciseId": %d, "orderIndex": 1},
                    {"exerciseId": %d, "orderIndex": 2}
                  ]
                }
                """.formatted(levelId, durationMinutes, status, exercise1Id, exercise2Id);
        String responseJson = mockMvc.perform(post("/api/admin/exams")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseJson).path("data");
    }

    @Test
    void nonAdmin_cannotCreateExam() throws Exception {
        JsonNode ex1 = createExercise("Q1");
        JsonNode ex2 = createExercise("Q2");
        String body = """
                {
                  "levelId": %d,
                  "title": "Nope",
                  "durationMinutes": 30,
                  "status": "PUBLISHED",
                  "questions": [
                    {"exerciseId": %d, "orderIndex": 1},
                    {"exerciseId": %d, "orderIndex": 2}
                  ]
                }
                """.formatted(levelId, ex1.path("id").asLong(), ex2.path("id").asLong());

        mockMvc.perform(post("/api/admin/exams")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void draftExam_hiddenFromUser_visibleToAdmin() throws Exception {
        JsonNode ex1 = createExercise("Q1");
        JsonNode ex2 = createExercise("Q2");
        JsonNode exam = createExam("DRAFT", 30, ex1.path("id").asLong(), ex2.path("id").asLong());
        long examId = exam.path("id").asLong();

        mockMvc.perform(get("/api/exams/" + examId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/exams/" + examId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(examId));

        mockMvc.perform(get("/api/exams").param("levelId", String.valueOf(levelId))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[?(@.id == " + examId + ")]").doesNotExist());
    }

    @Test
    void starting_draftExam_isRejected() throws Exception {
        JsonNode ex1 = createExercise("Q1");
        JsonNode ex2 = createExercise("Q2");
        JsonNode exam = createExam("DRAFT", 30, ex1.path("id").asLong(), ex2.path("id").asLong());

        mockMvc.perform(post("/api/exams/" + exam.path("id").asLong() + "/start")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void start_isIdempotent_resumesLiveAttempt() throws Exception {
        JsonNode ex1 = createExercise("Q1");
        JsonNode ex2 = createExercise("Q2");
        JsonNode exam = createExam("PUBLISHED", 30, ex1.path("id").asLong(), ex2.path("id").asLong());
        long examId = exam.path("id").asLong();

        String first = mockMvc.perform(post("/api/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long firstAttemptId = objectMapper.readTree(first).path("data").path("attemptId").asLong();

        String second = mockMvc.perform(post("/api/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long secondAttemptId = objectMapper.readTree(second).path("data").path("attemptId").asLong();

        org.junit.jupiter.api.Assertions.assertEquals(firstAttemptId, secondAttemptId);
    }

    @Test
    void submit_computesScoreServerSide_partialCredit() throws Exception {
        JsonNode ex1 = createExercise("Q1");
        JsonNode ex2 = createExercise("Q2");
        JsonNode exam = createExam("PUBLISHED", 30, ex1.path("id").asLong(), ex2.path("id").asLong());
        long examId = exam.path("id").asLong();

        String startJson = mockMvc.perform(post("/api/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode questions = objectMapper.readTree(startJson).path("data").path("questions");
        // Figure out which exam-question maps to which exercise so we can answer correctly for one, wrongly for the other.
        long examQuestionForEx1 = -1;
        long examQuestionForEx2 = -1;
        for (JsonNode q : questions) {
            long exerciseId = q.path("exercise").path("id").asLong();
            if (exerciseId == ex1.path("id").asLong()) {
                examQuestionForEx1 = q.path("id").asLong();
            } else if (exerciseId == ex2.path("id").asLong()) {
                examQuestionForEx2 = q.path("id").asLong();
            }
        }

        String submitBody = """
                {
                  "answers": [
                    {"examQuestionId": %d, "answerIds": [%d]},
                    {"examQuestionId": %d, "answerIds": [%d]}
                  ]
                }
                """.formatted(examQuestionForEx1, correctAnswerId(ex1), examQuestionForEx2, wrongAnswerId(ex2));

        mockMvc.perform(post("/api/exams/" + examId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(submitBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.correctCount").value(1))
                .andExpect(jsonPath("$.data.wrongCount").value(1))
                .andExpect(jsonPath("$.data.totalQuestions").value(2))
                .andExpect(jsonPath("$.data.score").value(50));
    }

    @Test
    void submit_pastDeadline_isRejectedAndMarksExpired() throws Exception {
        JsonNode ex1 = createExercise("Q1");
        JsonNode ex2 = createExercise("Q2");
        JsonNode exam = createExam("PUBLISHED", 10, ex1.path("id").asLong(), ex2.path("id").asLong());
        long examId = exam.path("id").asLong();

        String startJson = mockMvc.perform(post("/api/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long examQuestionId = objectMapper.readTree(startJson).path("data").path("questions").get(0).path("id").asLong();

        // Backdate the attempt so its deadline (startedAt + 10 min) is already past -
        // simulates BR-009 without an actual test sleep.
        ExamAttempt attempt = examAttemptRepository
                .findByUserIdAndExamIdAndStatus(userRepository.findByUsername("exam_user_" + testId).orElseThrow().getId(), examId, ExamAttemptStatus.IN_PROGRESS)
                .orElseThrow();
        attempt.setStartedAt(LocalDateTime.now().minusMinutes(20));
        examAttemptRepository.save(attempt);

        String submitBody = """
                {
                  "answers": [
                    {"examQuestionId": %d, "answerIds": [%d]}
                  ]
                }
                """.formatted(examQuestionId, correctAnswerId(ex1));

        mockMvc.perform(post("/api/exams/" + examId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content(submitBody))
                .andExpect(status().isBadRequest());

        ExamAttempt reloaded = examAttemptRepository.findById(attempt.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(ExamAttemptStatus.EXPIRED, reloaded.getStatus());
    }

    @Test
    void result_404sWithNoAttempt_thenReturnsAfterSubmit() throws Exception {
        JsonNode ex1 = createExercise("Q1");
        JsonNode ex2 = createExercise("Q2");
        JsonNode exam = createExam("PUBLISHED", 30, ex1.path("id").asLong(), ex2.path("id").asLong());
        long examId = exam.path("id").asLong();

        mockMvc.perform(get("/api/exams/" + examId + "/result")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());

        String startJson = mockMvc.perform(post("/api/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode questions = objectMapper.readTree(startJson).path("data").path("questions");

        StringBuilder answers = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            JsonNode q = questions.get(i);
            long exerciseId = q.path("exercise").path("id").asLong();
            JsonNode exercise = exerciseId == ex1.path("id").asLong() ? ex1 : ex2;
            if (i > 0) {
                answers.append(",");
            }
            answers.append("{\"examQuestionId\":").append(q.path("id").asLong())
                    .append(",\"answerIds\":[").append(correctAnswerId(exercise)).append("]}");
        }

        mockMvc.perform(post("/api/exams/" + examId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType("application/json")
                        .content("{\"answers\":[" + answers + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(100));

        mockMvc.perform(get("/api/exams/" + examId + "/result")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(100))
                .andExpect(jsonPath("$.data.correctCount").value(2));
    }
}
