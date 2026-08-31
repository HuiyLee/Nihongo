# Nihongo - Japanese Learning Website

A web application for learning Japanese from beginner to advanced (JLPT N5-N1).
Built per `docs/Japanese_Learning_Requirements.md`, following the incremental
development order the spec itself recommends (section 44).

**Current state: Phase 1 (Foundation) + Phase 2 (Content management)
done.** Everything below is implemented and working; the remaining features
(user-side vocabulary/Kanji/grammar learning + bookmarks, exercises, JLPT
exams, listening/reading/streak/spaced repetition, notifications, AI
features) are intentionally left for later phases and appear in the UI as a
"coming soon" placeholder so the full route map from the spec is already
wired up.

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
`CORS_ALLOWED_ORIGINS`.

> **Important:** the default `JWT_SECRET` in `application.yml` is a
> dev-only placeholder. Set a real, private, Base64-encoded 256-bit+ secret
> via the `JWT_SECRET` env var before deploying anywhere real.

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

See `docs/Japanese_Learning_Requirements.md` sections 38 and 44 for the full
roadmap: Phase 3 (user learning progress + Vocabulary flashcards + bookmarks
+ study sessions), Phase 4 (exercises), Phase 5 (JLPT exams), Phase 6
(listening/reading/streak/spaced repetition/notifications), Phase 7
(optional AI features).
