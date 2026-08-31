-- Phase 3: per-user learning state, bookmarks, study sessions

CREATE TABLE user_vocabulary (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    vocabulary_id    BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'NEW',
    correct_count    INTEGER     NOT NULL DEFAULT 0,
    wrong_count      INTEGER     NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMP,
    next_review_at   TIMESTAMP,
    created_at       TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_vocabulary UNIQUE (user_id, vocabulary_id),
    CONSTRAINT fk_user_vocabulary_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_vocabulary_vocabulary FOREIGN KEY (vocabulary_id) REFERENCES vocabularies (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_vocabulary_user_id ON user_vocabulary (user_id);

CREATE TABLE user_kanji (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    kanji_id         BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'NEW',
    correct_count    INTEGER     NOT NULL DEFAULT 0,
    wrong_count      INTEGER     NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMP,
    next_review_at   TIMESTAMP,
    created_at       TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_kanji UNIQUE (user_id, kanji_id),
    CONSTRAINT fk_user_kanji_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_kanji_kanji FOREIGN KEY (kanji_id) REFERENCES kanjis (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_kanji_user_id ON user_kanji (user_id);

CREATE TABLE user_grammar (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    grammar_id       BIGINT      NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'NEW',
    correct_count    INTEGER     NOT NULL DEFAULT 0,
    wrong_count      INTEGER     NOT NULL DEFAULT 0,
    last_reviewed_at TIMESTAMP,
    next_review_at   TIMESTAMP,
    created_at       TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_grammar UNIQUE (user_id, grammar_id),
    CONSTRAINT fk_user_grammar_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_grammar_grammar FOREIGN KEY (grammar_id) REFERENCES grammars (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_grammar_user_id ON user_grammar (user_id);

CREATE TABLE bookmarks (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id   BIGINT      NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT uk_bookmarks_target UNIQUE (user_id, target_type, target_id),
    CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_bookmarks_user_id ON bookmarks (user_id);

CREATE TABLE study_sessions (
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    started_at       TIMESTAMP   NOT NULL,
    ended_at         TIMESTAMP,
    duration_seconds INTEGER,
    activity_type    VARCHAR(20) NOT NULL,
    reference_id     BIGINT,
    created_at       TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT fk_study_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_study_sessions_user_id ON study_sessions (user_id);
CREATE INDEX idx_study_sessions_started_at ON study_sessions (started_at);
