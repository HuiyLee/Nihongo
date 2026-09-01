-- Phase 4: exercises + their answer options (requirements section 14)

CREATE TABLE exercises (
    id           BIGSERIAL PRIMARY KEY,
    lesson_id    BIGINT,
    level_id     BIGINT       NOT NULL,
    type         VARCHAR(30)  NOT NULL,
    question     TEXT         NOT NULL,
    explanation  TEXT,
    audio_url    VARCHAR(512),
    image_url    VARCHAR(512),
    difficulty   VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_exercises_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE SET NULL,
    CONSTRAINT fk_exercises_level FOREIGN KEY (level_id) REFERENCES levels (id)
);

CREATE INDEX idx_exercises_lesson_id ON exercises (lesson_id);
CREATE INDEX idx_exercises_level_id ON exercises (level_id);
CREATE INDEX idx_exercises_type ON exercises (type);

CREATE TABLE exercise_answers (
    id           BIGSERIAL PRIMARY KEY,
    exercise_id  BIGINT       NOT NULL,
    answer_text  TEXT         NOT NULL,
    is_correct   BOOLEAN      NOT NULL DEFAULT false,
    order_index  INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_exercise_answers_exercise FOREIGN KEY (exercise_id) REFERENCES exercises (id) ON DELETE CASCADE
);

CREATE INDEX idx_exercise_answers_exercise_id ON exercise_answers (exercise_id);
