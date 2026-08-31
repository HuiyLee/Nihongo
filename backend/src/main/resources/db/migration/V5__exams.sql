-- Phase 5: JLPT mock exams (requirements section 17-19)

CREATE TABLE exams (
    id               BIGSERIAL PRIMARY KEY,
    level_id         BIGINT       NOT NULL,
    title            VARCHAR(255) NOT NULL,
    description      TEXT,
    duration_minutes INTEGER      NOT NULL,
    total_questions  INTEGER      NOT NULL DEFAULT 0,
    status           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at       TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_exams_level FOREIGN KEY (level_id) REFERENCES levels (id)
);

CREATE INDEX idx_exams_level_id ON exams (level_id);
CREATE INDEX idx_exams_status ON exams (status);

CREATE TABLE exam_questions (
    id          BIGSERIAL PRIMARY KEY,
    exam_id     BIGINT    NOT NULL,
    exercise_id BIGINT    NOT NULL,
    order_index INTEGER   NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_exam_questions_exam FOREIGN KEY (exam_id) REFERENCES exams (id) ON DELETE CASCADE,
    CONSTRAINT fk_exam_questions_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id)
);

CREATE INDEX idx_exam_questions_exam_id ON exam_questions (exam_id);

CREATE TABLE exam_attempts (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    exam_id        BIGINT      NOT NULL,
    started_at     TIMESTAMP   NOT NULL,
    submitted_at   TIMESTAMP,
    status         VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    score          INTEGER     NOT NULL DEFAULT 0,
    correct_count  INTEGER     NOT NULL DEFAULT 0,
    wrong_count    INTEGER     NOT NULL DEFAULT 0,
    created_at     TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT fk_exam_attempts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_exam_attempts_exam FOREIGN KEY (exam_id) REFERENCES exams (id)
);

CREATE INDEX idx_exam_attempts_user_id ON exam_attempts (user_id);
CREATE INDEX idx_exam_attempts_exam_id ON exam_attempts (exam_id);
CREATE INDEX idx_exam_attempts_status ON exam_attempts (status);

CREATE TABLE exam_answers (
    id                 BIGSERIAL PRIMARY KEY,
    exam_attempt_id    BIGINT    NOT NULL,
    exam_question_id   BIGINT    NOT NULL,
    selected_answer_id BIGINT    NOT NULL,
    created_at         TIMESTAMP NOT NULL DEFAULT now(),
    updated_at         TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_exam_answers_attempt FOREIGN KEY (exam_attempt_id) REFERENCES exam_attempts (id) ON DELETE CASCADE,
    CONSTRAINT fk_exam_answers_question FOREIGN KEY (exam_question_id) REFERENCES exam_questions (id) ON DELETE CASCADE,
    CONSTRAINT fk_exam_answers_selected_answer FOREIGN KEY (selected_answer_id) REFERENCES exercise_answers (id)
);

CREATE INDEX idx_exam_answers_attempt_id ON exam_answers (exam_attempt_id);
