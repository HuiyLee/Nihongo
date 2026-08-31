# Nihongo - Japanese Learning Website

A web application for learning Japanese from beginner to advanced (JLPT N5-N1).
Built per `docs/Japanese_Learning_Requirements.md`, following the incremental
development order the spec itself recommends (section 44).

**Current state: Phase 1 - Foundation only.** Everything below is
implemented and working; every other feature in the requirements doc
(lessons, vocabulary, Kanji, grammar, exercises, exams, progress, streaks,
bookmarks, spaced repetition, ...) is intentionally left for later phases and
appears in the UI as a "coming soon" placeholder so the full route map from
the spec is already wired up.

## What's implemented (Phase 1)

- Project scaffolding for both frontend and backend, matching the folder
  structures in requirements sections 28-29.
- PostgreSQL via Docker Compose, with a Flyway migration creating
  `roles`, `users`, `refresh_tokens`.
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

Runs on `http://localhost:8080`. Flyway applies the migration automatically
on startup (`roles` table is seeded with `ROLE_ADMIN` / `ROLE_USER`).

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
3. To test the admin area, promote a user to `ROLE_ADMIN` directly in the
   database (`update users set role_id = (select id from roles where name =
   'ROLE_ADMIN') where username = '...'`) until Phase 2 adds an admin user
   management UI, then visit `/admin`.

## Making an admin user (Phase 1 has no admin UI yet)

```sql
UPDATE users SET role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE username = 'your_username';
```

## Next phases

See `docs/Japanese_Learning_Requirements.md` sections 38 and 44 for the full
roadmap: Phase 2 (Level/Lesson/Vocabulary/Kanji/Grammar admin CRUD), Phase 3
(user learning + bookmarks), Phase 4 (exercises), Phase 5 (JLPT exams),
Phase 6 (listening/reading/streak/spaced repetition/notifications), Phase 7
(optional AI features).
