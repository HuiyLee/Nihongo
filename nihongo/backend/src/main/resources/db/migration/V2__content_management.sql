-- Phase 2: Content management - levels, lessons, vocabularies, kanjis, grammars

CREATE TABLE levels (
    id          BIGSERIAL PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    order_index INTEGER      NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PUBLISHED',
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uk_levels_code UNIQUE (code),
    CONSTRAINT uk_levels_order_index UNIQUE (order_index)
);

CREATE TABLE lessons (
    id             BIGSERIAL PRIMARY KEY,
    level_id       BIGINT       NOT NULL,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    thumbnail_url  VARCHAR(512),
    order_index    INTEGER      NOT NULL,
    status         VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    created_at     TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_lessons_level FOREIGN KEY (level_id) REFERENCES levels (id)
);

CREATE INDEX idx_lessons_level_id ON lessons (level_id);
CREATE INDEX idx_lessons_status ON lessons (status);

CREATE TABLE vocabularies (
    id               BIGSERIAL PRIMARY KEY,
    lesson_id        BIGINT,
    level_id         BIGINT       NOT NULL,
    word             VARCHAR(255) NOT NULL,
    kanji            VARCHAR(255),
    hiragana         VARCHAR(255),
    katakana         VARCHAR(255),
    romaji           VARCHAR(255),
    meaning          TEXT         NOT NULL,
    part_of_speech   VARCHAR(100),
    example          TEXT,
    example_meaning  TEXT,
    audio_url        VARCHAR(512),
    image_url        VARCHAR(512),
    created_at       TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_vocabularies_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE SET NULL,
    CONSTRAINT fk_vocabularies_level FOREIGN KEY (level_id) REFERENCES levels (id)
);

CREATE INDEX idx_vocabularies_lesson_id ON vocabularies (lesson_id);
CREATE INDEX idx_vocabularies_level_id ON vocabularies (level_id);
CREATE INDEX idx_vocabularies_word ON vocabularies (word);
CREATE INDEX idx_vocabularies_kanji ON vocabularies (kanji);

CREATE TABLE kanjis (
    id                     BIGSERIAL PRIMARY KEY,
    lesson_id              BIGINT,
    level_id               BIGINT      NOT NULL,
    character              VARCHAR(10) NOT NULL,
    meaning                TEXT        NOT NULL,
    onyomi                 VARCHAR(255),
    kunyomi                VARCHAR(255),
    stroke_count           INTEGER,
    stroke_order_image_url VARCHAR(512),
    example                TEXT,
    example_meaning        TEXT,
    audio_url              VARCHAR(512),
    created_at             TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at             TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT fk_kanjis_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE SET NULL,
    CONSTRAINT fk_kanjis_level FOREIGN KEY (level_id) REFERENCES levels (id)
);

CREATE INDEX idx_kanjis_lesson_id ON kanjis (lesson_id);
CREATE INDEX idx_kanjis_level_id ON kanjis (level_id);
CREATE INDEX idx_kanjis_character ON kanjis (character);

CREATE TABLE grammars (
    id               BIGSERIAL PRIMARY KEY,
    lesson_id        BIGINT,
    level_id         BIGINT       NOT NULL,
    pattern          VARCHAR(255) NOT NULL,
    meaning          TEXT         NOT NULL,
    formation        TEXT,
    explanation      TEXT,
    example          TEXT,
    example_meaning  TEXT,
    notes            TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT fk_grammars_lesson FOREIGN KEY (lesson_id) REFERENCES lessons (id) ON DELETE SET NULL,
    CONSTRAINT fk_grammars_level FOREIGN KEY (level_id) REFERENCES levels (id)
);

CREATE INDEX idx_grammars_lesson_id ON grammars (lesson_id);
CREATE INDEX idx_grammars_level_id ON grammars (level_id);
CREATE INDEX idx_grammars_pattern ON grammars (pattern);

INSERT INTO levels (code, name, description, order_index, status) VALUES
    ('N5', 'JLPT N5', 'Basic Japanese - beginner level', 1, 'PUBLISHED'),
    ('N4', 'JLPT N4', 'Basic Japanese - elementary level', 2, 'PUBLISHED'),
    ('N3', 'JLPT N3', 'Intermediate Japanese', 3, 'PUBLISHED'),
    ('N2', 'JLPT N2', 'Upper-intermediate Japanese', 4, 'PUBLISHED'),
    ('N1', 'JLPT N1', 'Advanced Japanese', 5, 'PUBLISHED');
