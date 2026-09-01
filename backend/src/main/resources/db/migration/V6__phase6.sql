-- Phase 6: Reading passages, listening (reuses exercises), streak (computed,
-- no table), spaced repetition (reuses user_vocabulary/user_kanji/user_grammar's
-- existing next_review_at), notifications, dashboard statistics
-- (requirements sections 11, 15, 16, 20, 22, 24, 35).

CREATE TABLE readings (
    id             BIGSERIAL PRIMARY KEY,
    level_id       BIGINT       NOT NULL,
    title          VARCHAR(255) NOT NULL,
    content        TEXT         NOT NULL,
    translation    TEXT,
    difficulty     VARCHAR(20)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_readings_level FOREIGN KEY (level_id) REFERENCES levels (id)
);

CREATE INDEX idx_readings_level_id ON readings (level_id);

-- Section 16: exercises can now be attached to a reading passage, same
-- nullable-FK convention as exercises.lesson_id.
ALTER TABLE exercises ADD COLUMN reading_id BIGINT;
ALTER TABLE exercises ADD CONSTRAINT fk_exercises_reading FOREIGN KEY (reading_id) REFERENCES readings (id) ON DELETE SET NULL;
CREATE INDEX idx_exercises_reading_id ON exercises (reading_id);

CREATE TABLE notifications (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT,
    type       VARCHAR(30)  NOT NULL,
    is_read    BOOLEAN      NOT NULL DEFAULT false,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_notifications_user_id ON notifications (user_id);
CREATE INDEX idx_notifications_user_unread ON notifications (user_id, is_read);
