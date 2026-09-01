# Nihongo - Japanese Learning Website

A web application for learning Japanese from beginner to advanced (JLPT N5-N1).
Built per `docs/Japanese_Learning_Requirements.md`, following the incremental
development order the spec itself recommends (section 44).

**Current state: Phase 1 (Foundation) + Phase 2 (Content management) +
Phase 3 (User learning) + Phase 4 (Exercises) + Phase 5 (JLPT exams) +
Phase 6 (Advanced learning) + Phase 7 (AI features, partial) done.**
Every phase in the roadmap has at least an initial implementation. Phase 7
is explicitly optional per the spec and scoped to three of its six listed
AI features for this pass (grammar explanation, writing correction,
conversation practice) - AI-driven weakness analysis / personalized
learning path is intentionally left for a later iteration, matching the
spec's own "do not implement everything at once" rule.

## What's implemented

### Phase 1 - Foundation

- Project scaffolding for both frontend and backend, matching the folder
  structures in requirements sections 28-29.
- PostgreSQL via Docker Compose, with Flyway migrations.
- JWT-based authentication: register, login, refresh (with rotation), logout.
- Role-based authorization (`ROLE_ADMIN`, `ROLE_USER`) enforced by Spring
  Security on the backend - not just the frontend router.
- The standard `{status, message, data}` API response envelope on every
  endpoint, plus a global exception handler that maps custom exceptions to
  the right HTTP status codes without leaking stack traces.
- React + TypeScript + Vite frontend with React Router, Ant Design, a
  centralized Axios client (auto-attaches the JWT, auto-refreshes on 401),
  an auth context, and route guards for user/admin areas.
- Backend unit + integration tests covering register/login, duplicate
  username/email, invalid login, unauthenticated access, and forbidden
  admin access.

### Phase 2 - Content management (Level / Lesson / Vocabulary / Kanji / Grammar)

- Full admin CRUD for all five resources (`/api/admin/levels`,
  `/api/admin/lessons`, `/api/admin/vocabularies`, `/api/admin/kanji`,
  `/api/admin/grammars`) with validation and the standard pagination envelope
  (`{content, page, size, totalElements, totalPages}`, section 26).
- Public/authenticated read endpoints (`/api/levels`, `/api/lessons`,
  `/api/vocabularies`, `/api/kanji`, `/api/grammars`) with search + level/
  lesson filters. Draft lessons are only visible to admins (BR-006/BR-007) -
  regular users only ever see `PUBLISHED` lessons no matter what status they
  request.
- Vocabulary search matches word, Kanji, hiragana, katakana, romaji, and
  meaning in one `keyword` param.
- A single generic, reusable admin CRUD table+form component
  (`components/admin/CrudManager.tsx`) drives the Lesson/Vocabulary/Kanji/
  Grammar admin pages so the table/pagination/modal-form wiring exists once,
  not five times; Level management is a separate lightweight page since
  there are only ever 5 levels (N5-N1, already seeded by the migration).
- Integration tests covering level code/orderIndex uniqueness, admin-only
  write access (403 for regular users), the draft-lesson visibility rule,
  and vocabulary keyword search.

### Phase 3 - User learning (progress, bookmarks, study sessions)

- Per-user learning state on Vocabulary, Kanji, and Grammar (section 10):
  `GET /api/{vocabularies|kanji|grammars}/{id}/progress` and
  `POST /api/{vocabularies|kanji|grammars}/{id}/mark` (`{"outcome":"KNOWN"|"UNKNOWN"}`).
  Marking KNOWN/UNKNOWN updates `status`/`correctCount`/`wrongCount` -
  always scoped to the caller (never trusts a userId from the client). This
  is deliberately simple bookkeeping, not the real spaced-repetition
  algorithm - that belongs to a dedicated `SpacedRepetitionService` added in
  a later phase (section 11).
- Bookmarks (section 23 / BR-011): `POST/GET/DELETE /api/bookmarks`, plus a
  `GET /api/bookmarks/exists` convenience check. Vocabulary/Kanji/Grammar
  targets are validated to exist; duplicate bookmarks are rejected (409);
  `READING` is accepted by the data model's enum but rejected with a clear
  "not supported yet" error since there's no Reading entity yet.
- Study sessions (section 21): `POST/GET /api/study-sessions` records a
  finished study activity (`activityType`, optional `referenceId`,
  `startedAt`/`endedAt`; duration is computed server-side). This lays the
  groundwork for the streak feature (section 22), which isn't built yet -
  the API exists now so a later phase doesn't also have to touch the
  request layer; there's no frontend UI calling it yet.
- Frontend: real Vocabulary/Kanji/Grammar pages replace the "coming soon"
  placeholders - `/vocabulary`, `/kanji`, `/grammar` are searchable,
  level-filterable card grids; `/vocabulary/:id`, `/kanji/:id`,
  `/grammar/:id` are flip-card detail pages with Know/Still-learning
  buttons and a bookmark toggle. `/bookmarks` lists everything the user has
  bookmarked, filterable by type, with a remove action. All of this is
  built from two generic, reusable components
  (`components/learning/LearningBrowsePage.tsx`,
  `components/learning/FlashcardView.tsx`) so the browse/search/filter and
  flip-card/mark/bookmark logic exists once, not three times - the same
  pattern `CrudManager.tsx` uses on the admin side.
- Integration tests covering per-user isolation (marking an item for one
  user never affects another user's progress or bookmarks or study
  sessions), bookmark duplicate rejection, and the READING-not-supported
  and target-not-found error cases.

### Phase 4 - Exercises (multiple choice, multiple answer, true/false, fill in blank)

- Admin CRUD (`/api/admin/exercises`) for exercises, each with an owned list
  of answer options (`answerText`, `isCorrect`, `orderIndex` - section
  14.2/14.3). Updating an exercise replaces its answers wholesale rather
  than diffing, which keeps the "delete whatever fell out of the list"
  behavior (JPA `orphanRemoval`) simple and correct.
- Public read (`/api/exercises`) and `POST /api/exercises/{id}/submit`
  (section 14.4): submit takes `{"answerIds":[...]}` and returns
  `{correct, score, explanation}`. Grading is an exact-set match between
  the submitted answer ids and the stored correct ones, which works
  uniformly for single-answer and multi-answer questions without needing
  type-specific grading code.
- The one rule that matters most here: **a learner's response never
  contains `isCorrect`** on any answer option - only the admin-facing
  response does (`AdminExerciseResponse` vs `ExerciseResponse`, backed by
  separate mapper methods). Sending the answer key before submission would
  make the whole feature pointless, so this is covered by an integration
  test that asserts the field is absent, not just unused.
- `type` supports all six values from the spec's data model
  (`MULTIPLE_CHOICE`, `MULTIPLE_ANSWER`, `TRUE_FALSE`, `FILL_IN_BLANK`,
  `MATCHING`, `LISTENING`), but only the first four have an attempt UI in
  this phase, matching the Phase 4 scope in the requirements doc exactly;
  Matching/Listening exercises can be created by an admin but show a "not
  yet attemptable" message to learners instead of a broken UI.
- Frontend: `/admin/exercises` is a standalone admin page (not the generic
  `CrudManager`, since its answers are a dynamic list `CrudManager`'s flat
  field config can't express) with a `Form.List`-based editor for adding/
  removing/reordering answer rows. `/exercises` reuses the same
  `LearningBrowsePage` grid Vocabulary/Kanji/Grammar use; `/exercises/:id`
  is a dedicated attempt page (radio buttons for single-answer types,
  checkboxes for `MULTIPLE_ANSWER`) that submits and shows correct/
  incorrect plus the explanation.
- Integration tests: non-admin forbidden from admin exercise endpoints, the
  isCorrect-masking rule above, multiple-choice correct/incorrect grading,
  multiple-answer exact-set grading (partial credit is not correct), an
  answerId from a different exercise is rejected (400), and both "no
  answers on create" and "empty answerIds on submit" are rejected by
  validation.

### Phase 5 - JLPT exams

- Admin CRUD (`/api/admin/exams`) for exams, each with an owned, ordered list
  of questions (`exam_questions`) - every question just points at an
  existing `Exercise` (section 17.1) rather than duplicating question
  content, so the exam-authoring UI is "pick exercises, set the order," and
  grading reuses `ExerciseAnswer`'s `isCorrect` flags with zero new grading
  code. `totalQuestions` is never accepted from the request - the backend
  always computes it as `questions.size()` (same "don't trust the client for
  a derived value" rule as the exam score, below).
- Public browsing (`/api/exams`) reuses the Lesson/Exercise draft-visibility
  pattern (BR-006/007/008): regular users only ever see `PUBLISHED` exams,
  admins previewing the same endpoint see every status. Unlike Exercise,
  there's no separate admin/public DTO split needed for *browsing* - the
  public `ExamResponse` is a flat summary (id/title/duration/totalQuestions/
  status/...) that never nests question content at all, so there's nothing
  for it to leak regardless of who's asking. Nested question content (with
  the isCorrect-masking `AdminExamResponse` vs the masked
  `ExamAttemptResponse`) only exists in the admin-CRUD and attempt-taking
  responses respectively.
- The exam-taking flow (section 18, `POST /api/exams/{id}/start`,
  `POST /api/exams/{id}/submit`, `GET /api/exams/{id}/result`):
  - Starting a `DRAFT`/`ARCHIVED` exam is rejected (BR-008). Starting is
    idempotent - if the caller already has a live `IN_PROGRESS` attempt, it's
    resumed (same attempt id, same questions) instead of creating a
    duplicate.
  - **The backend, never the frontend, is the source of truth for the exam
    clock** (explicit requirement, section 18: "the backend must not trust
    the frontend timer"). The deadline is always recomputed server-side from
    the DB-stored `startedAt + exam.durationMinutes` on every `start`/
    `submit` call; a submit past that deadline marks the attempt `EXPIRED`
    and is rejected (400, BR-009) rather than graded, no matter what the
    client's own countdown claims. The frontend timer is purely a UX
    countdown that auto-submits at zero as a convenience - the server would
    reject a late submit even if that client-side code were buggy or
    bypassed entirely.
  - Grading (BR-010) reuses the exact same exact-set-match algorithm as
    Exercise submission, just looped per question: the submitted answer-id
    set must equal the correct answer-id set for a question to count as
    correct. `score`/`correctCount`/`wrongCount` are always computed
    server-side from that loop - a submit request never carries a score for
    the server to trust. `GET /result` returns the latest concluded
    (`COMPLETED` or `EXPIRED`) attempt, 404 if the learner hasn't finished
    one yet.
  - **Auto save** (`PUT /api/exams/{id}/save`): persists whatever's
    currently selected without grading or changing the attempt's status,
    subject to the same BR-009 deadline check as submit. `start()` returns
    whatever was last saved (`savedAnswers`) when it resumes a live attempt,
    so a page refresh mid-exam restores prior selections instead of losing
    them. `submit()` and `saveProgress()` share one validate/persist helper
    internally so there's exactly one place that decides whether an
    `examQuestionId`/`answerIds` pair is legal for a given exam.
  - **History** (`GET /api/exams/attempts/history`): every concluded
    attempt across every exam for the caller, newest first, paginated -
    always scoped to the authenticated user, same as every other attempt
    endpoint.
- Frontend: `/admin/exams` is a standalone admin page (same reasoning as
  `/admin/exercises` - a dynamic `Form.List` of questions doesn't fit the
  generic `CrudManager`), where each question row is a searchable dropdown
  of existing exercises rather than an inline editor. `/exams` reuses
  `LearningBrowsePage` (with a link to `/exams/history`); `/exams/:id` shows
  the exam summary with a Start button, then switches in place to the
  question list with a live countdown once started, debounce-autosaving
  selections every ~1.5s and auto-submitting at zero; `/exams/:id/result`
  shows the score, correct/wrong counts, and a Retake button;
  `/exams/history` lists every past attempt with a link back to its result.
- Integration tests: non-admin forbidden from admin exam endpoints, a draft
  exam is invisible to a regular user (404) but visible to an admin,
  starting a draft exam is rejected, starting twice resumes the same
  attempt, submitting computes partial-credit scoring correctly, submitting
  after the deadline (simulated by backdating the attempt's `startedAt`
  directly in the test rather than an actual multi-minute sleep) is
  rejected and marks the attempt `EXPIRED`, `GET /result` 404s with no
  finished attempt and returns the right data after one, saving progress
  then resuming (a second `start()` call) echoes the saved selection back,
  saving past the deadline is rejected the same way submitting is, and
  `GET /attempts/history` only ever returns the calling user's own
  concluded attempts (verified against a second, freshly-registered user
  with none).

### Phase 6 - Advanced learning (reading, listening, spaced repetition, streak, notifications, progress)

- **Reading** (section 16): a new `Reading` entity (level, title, HTML
  `content`, optional `translation`, difficulty) with public browse/detail
  (`/api/readings`) and admin CRUD (`/api/admin/readings`). Unlike Lesson/
  Exam there's no draft/published split - every reading is visible as soon
  as it's created, matching the spec's scope for this content type.
  Furigana is authored directly in the HTML as `<ruby>漢字<rt>かんじ</rt></ruby>`
  and toggled client-side with a CSS class, so the backend never parses
  furigana at all. The `translation` field is only ever included in the
  response once the caller has completed the passage (or is an admin) -
  completion is tracked by checking for a matching study session, not a
  separate "completed" column. `POST /api/readings/{id}/complete` is
  idempotent and records a study session (see below). Exercises can
  optionally link to a Reading (`readingId`) so admins can attach
  comprehension questions to a passage; the Reading detail page lists and
  links to them.
- **Listening** (section 15): no new backend entity - fully reuses the
  existing `Exercise`/`ExerciseType.LISTENING`/`audioUrl`. A new shared
  `AudioPlayer` component (play/pause/replay/five fixed speeds: 0.5x-1.5x)
  is used both on the exercise attempt page and inline per-question on the
  exam attempt page wherever a question carries audio. `/listening` is the
  same `LearningBrowsePage` grid the other content types use, pre-filtered
  to `type: LISTENING`, reusing the existing exercise attempt flow rather
  than a separate one.
- **Spaced repetition** (section 11): a real `SpacedRepetitionService`
  replaces the placeholder bookkeeping from Phase 3. Marking an item KNOWN
  doubles the previous review interval (starting at 1 day, capped at 90);
  marking it UNKNOWN halves the interval (floored at 1 day) and resets the
  item to `LEARNING`. The previous interval is derived from the gap between
  the item's existing `lastReviewedAt`/`nextReviewAt` timestamps, so no
  schema change was needed.
- **Study activity tracking**: every learning action (marking vocabulary/
  kanji/grammar, submitting a listening exercise, completing a reading,
  finishing an exam) now records a row via the study-session API that
  already existed from Phase 3 but had no callers until this phase. This
  single source of truth feeds both the streak and the progress dashboard.
- **Streak** (section 22, `GET /api/streak`): current and longest daily
  study streak, computed on the fly from study-session timestamps (never
  cached, and bucketed to calendar days in plain Java rather than
  DB-specific date functions, so it behaves the same on H2 in tests and
  Postgres in production).
- **Notifications** (section 24): a new `Notification` entity/CRUD
  (`/api/notifications`, unread count, mark-read, mark-all-read) plus a
  `NotificationBell` in the header that polls every 60s. Publishing a
  lesson or exam for the first time (draft/none -> published) fans a
  notification out to every user automatically.
- **Progress dashboard** (section 20, `GET /api/progress/*`) and **admin
  stats** (section 35, `GET /api/admin/stats`): real, always-recomputed
  percentages per category (vocabulary/kanji/grammar/lessons/exams) replace
  the placeholder 0% cards on both the learner dashboard and the admin
  overview page, plus the learner's current streak and admin-facing
  platform totals and exam pass rate.
- Integration tests covering: reading translation visibility before/after
  completion (and that completing twice doesn't double-record), spaced
  repetition interval progression across consecutive KNOWN/UNKNOWN marks,
  streak counting across consecutive and non-consecutive days, a
  publish-triggered notification actually reaching a user's notification
  list, and the progress endpoints reflecting real marked/completed state.

### Phase 7 - AI features (optional, partial)

- Scoped to three of the six AI features the spec lists - **AI grammar
  explanation**, **AI writing correction**, and **AI conversation
  practice** - chosen because they're the highest learner value at the
  lowest scope; weakness analysis / a personalized learning path is left
  for later, per the same "don't build everything at once" rule that
  shaped every earlier phase.
- All three are backed by a single `AnthropicClient` (Anthropic's Messages
  API), the one place in the codebase that knows the wire format so every
  AI feature shares one call/error-handling path instead of duplicating
  HTTP logic. It's configured via `ANTHROPIC_API_KEY` (blank by default) -
  **the app starts and every other feature works normally with no key
  set**; only the three `/api/ai/*` endpoints fail, with a clear 503
  ("AI features are not configured on this server"), never a startup
  crash or a leaked stack trace. `ANTHROPIC_MODEL` (default
  `claude-sonnet-4-5`) and `ANTHROPIC_MAX_TOKENS` (default `1024`) are
  also overridable - check Anthropic's current model list if the default
  alias ever changes.
- `POST /api/ai/grammar-explanation`: takes an optional `grammarId` (grounds
  the explanation in an existing Grammar entity's pattern/meaning/formation,
  echoed back in the response) and/or a free-text `question` - at least one
  is required. Reached standalone or via an "Explain with AI" button on the
  Grammar flashcard page.
- `POST /api/ai/writing-correction`: takes Japanese `text`, asks the model
  for a corrected version plus feedback in a fixed two-section format, and
  parses that into `{corrected, feedback}` - if the model ever ignores the
  format, the raw reply is still surfaced as feedback rather than the
  request failing outright.
- `POST /api/ai/conversation`: deliberately stateless - no new entity or
  migration. The frontend holds the running chat in React state only and
  resends the full message history every turn; the backend just adds a
  level-appropriate system prompt (JLPT `level`, default N5) and forwards
  it to Claude.
- Integration tests mock `AnthropicClient` (via `@MockBean`) so the
  controller/validation/auth layer is tested without hitting the real API
  or requiring a key; a separate unit test exercises the real "no API key
  configured" 503 path; and a further unit test suite covers `AiService`'s
  own logic (grammar lookup, required-field validation, the
  corrected/feedback marker parsing) with `AnthropicClient` mocked out.

## Project layout

```
backend/    Spring Boot 3 (Java 17), Maven
frontend/   React 19 + TypeScript + Vite
docker-compose.yml   PostgreSQL for local development
```

## Running locally

### 1. Database

```bash
docker compose up -d
```

This starts PostgreSQL on `localhost:5432` (db `nihongo`, user/password
`nihongo`/`nihongo` - see `docker-compose.yml`, override via env vars for
anything beyond local dev).

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

Runs on `http://localhost:8080`. Flyway applies migrations automatically on
startup (`roles` seeded with `ROLE_ADMIN` / `ROLE_USER`; `levels` seeded with
N5-N1).

Environment variables (all optional, see `application.yml` for defaults):
`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`,
`JWT_ACCESS_EXPIRATION_MS`, `JWT_REFRESH_EXPIRATION_MS`,
`CORS_ALLOWED_ORIGINS`, `ANTHROPIC_API_KEY`, `ANTHROPIC_MODEL`,
`ANTHROPIC_MAX_TOKENS`.

> **Important:** the default `JWT_SECRET` in `application.yml` is a
> dev-only placeholder. Set a real, private, Base64-encoded 256-bit+ secret
> via the `JWT_SECRET` env var before deploying anywhere real.

> **Optional:** the Phase 7 AI endpoints (`/api/ai/*`) need `ANTHROPIC_API_KEY`
> set to an Anthropic API key to actually work. Everything else in the app
> runs fully without it.

Run the test suite:

```bash
mvn test
```

### 3. Frontend

```bash
cd frontend
cp .env.example .env   # adjust VITE_API_BASE_URL if the backend isn't on :8080
npm install
npm run dev
```

Runs on `http://localhost:5173`.

Other scripts: `npm run build`, `npm run lint`, `npm run format`.

### 4. Try it

1. Open `http://localhost:5173`, click "Get started", register a user.
2. Log in - you land on `/dashboard`.
3. To test the admin area, promote a user to `ROLE_ADMIN` (see below), then
   visit `/admin` and manage Levels, Lessons, Vocabulary, Kanji and Grammar.

## Making an admin user (Phase 2 has no admin user-management UI yet)

```sql
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE username = 'your_username';
```

## Next phases

Every phase in `docs/Japanese_Learning_Requirements.md` sections 38/44 has
an initial implementation. What's left is optional depth within Phase 7:
AI-driven weakness analysis / a personalized learning path (the two AI
features not built in this pass), and AI speaking practice (needs a
speech/audio pipeline beyond this phase's text-based scope).
