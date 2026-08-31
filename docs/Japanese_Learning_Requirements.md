# Japanese Learning Website -- Vibe Coding Requirements

## 1. Project Overview

### 1.1. Project Name

Japanese Learning Website

### 1.2. Purpose

Build a web application that helps users learn Japanese from beginner to
advanced levels, with a focus on JLPT N5--N1.

The system must support:

-   Japanese lessons
-   Vocabulary
-   Kanji
-   Grammar
-   Listening
-   Reading
-   Exercises
-   JLPT mock exams
-   Progress tracking
-   Flashcards
-   Spaced repetition
-   Bookmarks
-   Learning streaks

The application must be designed so that new learning content can be
added easily without modifying application logic.

------------------------------------------------------------------------

# 2. Tech Stack

## 2.1. Frontend

Use:

-   React
-   TypeScript
-   Vite
-   React Router
-   Axios
-   Ant Design or Material UI
-   ESLint
-   Prettier

Frontend responsibilities:

-   UI rendering
-   Client-side routing
-   Authentication state
-   API communication
-   Form validation
-   User interaction
-   Learning progress display

## 2.2. Backend

Use:

-   Java 17
-   Spring Boot
-   Spring Security
-   JWT
-   Spring Data JPA
-   Hibernate
-   Maven
-   Bean Validation

Backend architecture:

``` text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Use DTOs between Controller and Service.

Do not expose JPA Entity objects directly through REST APIs.

## 2.3. Database

Use:

-   PostgreSQL

Database requirements:

-   Primary keys
-   Foreign keys
-   Unique constraints
-   NOT NULL constraints where appropriate
-   Indexes for frequently searched fields
-   created_at / updated_at for major entities

------------------------------------------------------------------------

# 3. User Roles

The system has two roles.

## 3.1. ADMIN

Admin can:

-   Manage users
-   Manage JLPT levels
-   Manage lessons
-   Manage vocabulary
-   Manage Kanji
-   Manage grammar
-   Manage exercises
-   Manage exams
-   View system statistics

## 3.2. USER

User can:

-   Register
-   Login
-   View lessons
-   Learn vocabulary
-   Learn Kanji
-   Learn grammar
-   Practice listening
-   Practice reading
-   Complete exercises
-   Take JLPT exams
-   View learning progress
-   Manage bookmarks
-   View learning streak
-   Manage profile

------------------------------------------------------------------------

# 4. Authentication

## 4.1. Register

Endpoint:

``` http
POST /api/auth/register
```

Request:

``` json
{
  "username": "user01",
  "email": "user@example.com",
  "password": "password",
  "fullName": "User"
}
```

Validation:

-   username is required
-   username must be unique
-   email is required
-   email must be valid
-   email must be unique
-   password is required
-   password must satisfy security requirements

Password must never be stored as plaintext.

Use BCrypt.

## 4.2. Login

Endpoint:

``` http
POST /api/auth/login
```

Request:

``` json
{
  "username": "user01",
  "password": "password"
}
```

Response:

``` json
{
  "status": "SUCCESS",
  "message": "Login successful",
  "data": {
    "accessToken": "...",
    "refreshToken": "...",
    "user": {
      "id": 1,
      "username": "user01",
      "role": "USER"
    }
  }
}
```

## 4.3. Refresh Token

``` http
POST /api/auth/refresh
```

## 4.4. Logout

``` http
POST /api/auth/logout
```

Authentication must use:

-   Access Token
-   Refresh Token
-   JWT
-   Spring Security

Protected APIs must reject unauthenticated requests.

------------------------------------------------------------------------

# 5. Authorization

Admin APIs must require:

``` text
ROLE_ADMIN
```

User APIs must require:

``` text
ROLE_USER
```

Admin must also be able to access normal user learning functionality if
required by the UI.

Do not rely only on frontend route protection.

Authorization must be enforced by the backend.

------------------------------------------------------------------------

# 6. Standard API Response

All APIs must follow a consistent response structure.

## 6.1. Success

``` json
{
  "status": "SUCCESS",
  "message": "Success",
  "data": {}
}
```

## 6.2. Error

``` json
{
  "status": "ERROR",
  "message": "Vocabulary not found",
  "data": null
}
```

HTTP status codes must still be used correctly.

Examples:

``` text
200 OK
201 CREATED
400 BAD_REQUEST
401 UNAUTHORIZED
403 FORBIDDEN
404 NOT_FOUND
409 CONFLICT
500 INTERNAL_SERVER_ERROR
```

------------------------------------------------------------------------

# 7. JLPT Level

Supported levels:

``` text
N5
N4
N3
N2
N1
```

Entity:

``` text
Level
```

Fields:

``` text
id
code
name
description
orderIndex
status
createdAt
updatedAt
```

Constraints:

-   code must be unique
-   orderIndex must be unique

------------------------------------------------------------------------

# 8. Lesson Management

## 8.1. Lesson

A lesson belongs to one JLPT level.

Fields:

``` text
id
levelId
title
description
thumbnailUrl
orderIndex
status
createdAt
updatedAt
```

## 8.2. Admin APIs

``` http
GET    /api/admin/lessons
GET    /api/admin/lessons/{id}
POST   /api/admin/lessons
PUT    /api/admin/lessons/{id}
DELETE /api/admin/lessons/{id}
```

## 8.3. User APIs

``` http
GET /api/lessons
GET /api/lessons/{id}
```

User must be able to filter lessons by:

-   JLPT level
-   status

------------------------------------------------------------------------

# 9. Vocabulary

## 9.1. Vocabulary Fields

``` text
id
lessonId
levelId
word
kanji
hiragana
katakana
romaji
meaning
partOfSpeech
example
exampleMeaning
audioUrl
imageUrl
createdAt
updatedAt
```

## 9.2. Requirements

User can:

-   Search vocabulary
-   Filter by JLPT
-   Filter by lesson
-   View vocabulary detail
-   Play pronunciation
-   Add bookmark
-   Mark as known/unknown

Search must support:

-   Japanese word
-   Kanji
-   Hiragana
-   Katakana
-   Romaji
-   Vietnamese meaning

## 9.3. Admin APIs

``` http
GET    /api/admin/vocabularies
GET    /api/admin/vocabularies/{id}
POST   /api/admin/vocabularies
PUT    /api/admin/vocabularies/{id}
DELETE /api/admin/vocabularies/{id}
```

------------------------------------------------------------------------

# 10. Vocabulary Learning

User learning state must be stored independently for each user.

Entity:

``` text
UserVocabulary
```

Fields:

``` text
id
userId
vocabularyId
status
correctCount
wrongCount
lastReviewedAt
nextReviewAt
createdAt
updatedAt
```

Status:

``` text
NEW
LEARNING
KNOWN
REVIEW
```

When the user marks a vocabulary item:

``` text
KNOWN
UNKNOWN
```

the system updates the learning state.

------------------------------------------------------------------------

# 11. Spaced Repetition

The system should support spaced repetition.

Basic behavior:

``` text
New vocabulary
      ↓
Learning
      ↓
User answers correctly
      ↓
Increase review interval
      ↓
Schedule next review
```

If the user answers incorrectly:

``` text
Decrease review interval
      ↓
Schedule earlier review
```

The exact algorithm must be implemented in a dedicated service:

``` text
SpacedRepetitionService
```

Do not put spaced repetition logic inside Controller.

The algorithm should be replaceable in the future.

------------------------------------------------------------------------

# 12. Kanji

## 12.1. Fields

``` text
id
lessonId
levelId
character
meaning
onyomi
kunyomi
strokeCount
strokeOrderImageUrl
example
exampleMeaning
audioUrl
createdAt
updatedAt
```

## 12.2. User Features

User can:

-   Search Kanji
-   Filter by JLPT
-   Filter by lesson
-   View details
-   Listen to pronunciation
-   Bookmark Kanji
-   Mark learning status

## 12.3. APIs

``` http
GET    /api/kanji
GET    /api/kanji/{id}

GET    /api/admin/kanji
POST   /api/admin/kanji
PUT    /api/admin/kanji/{id}
DELETE /api/admin/kanji/{id}
```

------------------------------------------------------------------------

# 13. Grammar

## 13.1. Fields

``` text
id
lessonId
levelId
pattern
meaning
formation
explanation
example
exampleMeaning
notes
createdAt
updatedAt
```

## 13.2. User Features

User can:

-   Search grammar
-   Filter by JLPT
-   Filter by lesson
-   View grammar details
-   Bookmark grammar
-   Practice grammar exercises

## 13.3. APIs

``` http
GET    /api/grammars
GET    /api/grammars/{id}

GET    /api/admin/grammars
POST   /api/admin/grammars
PUT    /api/admin/grammars/{id}
DELETE /api/admin/grammars/{id}
```

------------------------------------------------------------------------

# 14. Exercises

## 14.1. Exercise Types

Support:

``` text
MULTIPLE_CHOICE
MULTIPLE_ANSWER
TRUE_FALSE
FILL_IN_BLANK
MATCHING
LISTENING
```

## 14.2. Exercise Fields

``` text
id
lessonId
levelId
type
question
explanation
audioUrl
imageUrl
difficulty
createdAt
updatedAt
```

## 14.3. Answer Fields

``` text
id
exerciseId
answerText
isCorrect
orderIndex
```

## 14.4. Submit Exercise

``` http
POST /api/exercises/{id}/submit
```

Request:

``` json
{
  "answerIds": [1]
}
```

Response:

``` json
{
  "status": "SUCCESS",
  "message": "Exercise submitted",
  "data": {
    "correct": true,
    "score": 1,
    "explanation": "..."
  }
}
```

------------------------------------------------------------------------

# 15. Listening

User can:

-   Play audio
-   Pause
-   Replay
-   Change playback speed
-   Answer questions

Supported speed:

``` text
0.5x
0.75x
1.0x
1.25x
1.5x
```

Audio files must be referenced through URLs or an object-storage
service.

Do not store large binary audio files directly inside PostgreSQL.

------------------------------------------------------------------------

# 16. Reading

Reading lesson contains:

``` text
id
levelId
title
content
translation
difficulty
createdAt
updatedAt
```

User can:

-   Read passage
-   View Furigana
-   Answer questions
-   View translation after completing the exercise
-   View explanations

------------------------------------------------------------------------

# 17. JLPT Exam

## 17.1. Exam

Fields:

``` text
id
levelId
title
description
durationMinutes
totalQuestions
status
createdAt
updatedAt
```

Status:

``` text
DRAFT
PUBLISHED
ARCHIVED
```

## 17.2. Exam Question

Fields:

``` text
id
examId
exerciseId
orderIndex
```

## 17.3. Exam APIs

``` http
GET /api/exams
GET /api/exams/{id}

POST /api/exams/{id}/start
POST /api/exams/{id}/submit
GET /api/exams/{id}/result
```

------------------------------------------------------------------------

# 18. Exam Flow

``` text
User selects exam
        ↓
Start exam
        ↓
Create ExamAttempt
        ↓
Display questions
        ↓
User answers
        ↓
Auto-save answers
        ↓
Submit
        ↓
Calculate result
        ↓
Save result
        ↓
Display result
```

If the timer reaches zero:

``` text
Auto Submit
```

The backend must not trust the frontend timer.

The backend must validate the exam start time and allowed duration.

------------------------------------------------------------------------

# 19. Exam Attempt

Entity:

``` text
ExamAttempt
```

Fields:

``` text
id
userId
examId
startedAt
submittedAt
status
score
correctCount
wrongCount
```

Status:

``` text
IN_PROGRESS
COMPLETED
EXPIRED
```

------------------------------------------------------------------------

# 20. Learning Progress

The system must track progress per user.

Progress includes:

``` text
Lesson
Vocabulary
Kanji
Grammar
Listening
Reading
Exam
```

APIs:

``` http
GET /api/progress
GET /api/progress/vocabulary
GET /api/progress/kanji
GET /api/progress/grammar
GET /api/progress/lessons
GET /api/progress/exams
```

Dashboard example:

``` text
Vocabulary   80%
Kanji        60%
Grammar      50%
Lessons      75%
```

------------------------------------------------------------------------

# 21. Study Session

Each learning session can be recorded.

Fields:

``` text
id
userId
startedAt
endedAt
durationSeconds
activityType
referenceId
```

Activity types:

``` text
LESSON
VOCABULARY
KANJI
GRAMMAR
LISTENING
READING
EXAM
```

------------------------------------------------------------------------

# 22. Streak

The system tracks consecutive study days.

Example:

``` text
Current streak: 7 days
Longest streak: 21 days
```

A day is considered a study day if the user performs at least one
learning activity.

------------------------------------------------------------------------

# 23. Bookmark

User can bookmark:

-   Vocabulary
-   Kanji
-   Grammar
-   Reading

Entity:

``` text
Bookmark
```

Fields:

``` text
id
userId
targetType
targetId
createdAt
```

targetType:

``` text
VOCABULARY
KANJI
GRAMMAR
READING
```

Constraint:

``` text
(userId, targetType, targetId)
```

must be unique.

------------------------------------------------------------------------

# 24. Notification

Notifications may include:

-   New lesson
-   New exam
-   Vocabulary review reminder
-   Learning streak
-   System notification

Fields:

``` text
id
userId
title
content
type
isRead
createdAt
```

------------------------------------------------------------------------

# 25. Search

Global search should support:

``` text
Vocabulary
Kanji
Grammar
Lesson
Reading
```

Search should support Japanese and Vietnamese text.

For large datasets, use database indexes and pagination.

------------------------------------------------------------------------

# 26. Pagination

List APIs must support pagination.

Example:

``` http
GET /api/vocabularies?page=0&size=20
```

Optional parameters:

``` text
page
size
sort
keyword
level
lessonId
status
```

Response:

``` json
{
  "status": "SUCCESS",
  "message": "Success",
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  }
}
```

------------------------------------------------------------------------

# 27. Frontend Pages

## Public Pages

``` text
/
 /login
 /register
```

## User Pages

``` text
/dashboard
/lessons
/lessons/:id
/vocabulary
/vocabulary/:id
/kanji
/kanji/:id
/grammar
/grammar/:id
/listening
/reading
/exercises
/exams
/exams/:id
/exams/:id/result
/progress
/bookmarks
/profile
```

## Admin Pages

``` text
/admin
/admin/users
/admin/levels
/admin/lessons
/admin/vocabulary
/admin/kanji
/admin/grammar
/admin/exercises
/admin/exams
```

------------------------------------------------------------------------

# 28. Frontend Architecture

Recommended structure:

``` text
src/
├── api/
├── assets/
├── components/
├── contexts/
├── hooks/
├── layouts/
├── pages/
├── routes/
├── services/
├── types/
├── utils/
├── App.tsx
└── main.tsx
```

Use reusable components.

Do not duplicate API logic inside pages.

API calls should be centralized.

------------------------------------------------------------------------

# 29. Backend Architecture

Recommended structure:

``` text
src/main/java/
└── com.example.japanese/
    ├── config/
    ├── controller/
    ├── service/
    ├── repository/
    ├── entity/
    ├── dto/
    │   ├── request/
    │   └── response/
    ├── mapper/
    ├── security/
    ├── exception/
    ├── validation/
    └── util/
```

Business logic belongs in Service.

Database operations belong in Repository.

Authentication logic belongs in Security.

Global exception handling should use:

``` text
@RestControllerAdvice
```

------------------------------------------------------------------------

# 30. Database Schema

Main entities:

``` text
users
roles
levels
lessons
vocabularies
kanjis
grammars
exercises
exercise_answers
exams
exam_questions
exam_attempts
exam_answers
user_vocabulary
user_kanji
user_grammar
study_sessions
bookmarks
notifications
```

Main relationships:

``` text
Role
  1
  |
  N
User

Level
  1
  |
  N
Lesson

Lesson
  |
  ├── Vocabulary
  ├── Kanji
  ├── Grammar
  └── Exercise

Exam
  |
  └── ExamQuestion
          |
          └── Exercise

User
  |
  ├── UserVocabulary
  ├── UserKanji
  ├── UserGrammar
  ├── ExamAttempt
  ├── StudySession
  ├── Bookmark
  └── Notification
```

------------------------------------------------------------------------

# 31. Validation

Backend must validate all incoming requests.

Examples:

``` text
username: required, unique
email: required, valid email
password: required, minimum length
title: required
JLPT level: required
vocabulary word: required
grammar pattern: required
```

Frontend validation is for user experience.

Backend validation is mandatory for security and data integrity.

------------------------------------------------------------------------

# 32. Error Handling

Create application-specific exceptions.

Examples:

``` text
ResourceNotFoundException
DuplicateResourceException
InvalidRequestException
UnauthorizedException
ForbiddenException
ExamExpiredException
```

Use a global exception handler.

Do not expose stack traces to users.

------------------------------------------------------------------------

# 33. Security Requirements

-   Passwords must use BCrypt.
-   JWT must have expiration.
-   Refresh tokens must have expiration.
-   Protected endpoints must require authentication.
-   Admin endpoints must require ADMIN role.
-   Never return password hashes.
-   Validate all request data.
-   Prevent unauthorized access to another user's progress.
-   User A must not be able to access User B's private data by changing
    an ID in the URL.
-   Backend must verify ownership of user-specific resources.

------------------------------------------------------------------------

# 34. Logging

Log important operations:

``` text
LOGIN
LOGOUT
REGISTER
ADMIN_CREATE
ADMIN_UPDATE
ADMIN_DELETE
EXAM_START
EXAM_SUBMIT
SYSTEM_ERROR
```

Do not log:

-   Passwords
-   Access tokens
-   Refresh tokens
-   Sensitive authentication data

------------------------------------------------------------------------

# 35. Admin Dashboard

Display:

``` text
Total Users
Total Lessons
Total Vocabulary
Total Kanji
Total Grammar
Total Exercises
Total Exams
```

Statistics:

``` text
User registrations
Learning sessions
Exam attempts
Pass/fail rate
```

------------------------------------------------------------------------

# 36. Business Rules

## BR-001

Username must be unique.

## BR-002

Email must be unique.

## BR-003

Only ADMIN can modify learning content.

## BR-004

USER can only modify their own profile.

## BR-005

USER can only access their own learning progress.

## BR-006

Published lessons are visible to users.

## BR-007

Draft lessons are only visible to ADMIN.

## BR-008

Only published exams can be taken by users.

## BR-009

An expired exam attempt cannot be submitted normally.

## BR-010

Exam submission must be calculated on the backend.

## BR-011

A user cannot create duplicate bookmarks for the same target.

## BR-012

Learning progress must belong to exactly one user and one learning item.

------------------------------------------------------------------------

# 37. Acceptance Criteria

## Authentication

-   User can register successfully.
-   User cannot register with an existing username.
-   User cannot register with an existing email.
-   User can login with valid credentials.
-   Invalid credentials return 401.
-   Protected APIs reject unauthenticated requests.

## Vocabulary

-   Admin can CRUD vocabulary.
-   User can search vocabulary.
-   User can filter vocabulary.
-   User can view vocabulary detail.
-   User can mark vocabulary as known/unknown.
-   Learning state is persisted.

## Kanji

-   Admin can CRUD Kanji.
-   User can search Kanji.
-   User can view Kanji details.
-   User can bookmark Kanji.

## Grammar

-   Admin can CRUD grammar.
-   User can search grammar.
-   User can view grammar details.
-   User can bookmark grammar.

## Exercise

-   User can start an exercise.
-   User can submit an answer.
-   Backend calculates correctness.
-   User can see explanation.

## Exam

-   User can start published exams.
-   Exam timer is enforced.
-   Answers are saved.
-   User can submit the exam.
-   Backend calculates score.
-   User can view result history.

## Progress

-   Learning activity updates progress.
-   Dashboard displays current progress.
-   Study streak is updated after learning activity.

------------------------------------------------------------------------

# 38. Development Phases

## Phase 1 -- Foundation

Implement:

-   Project setup
-   Database
-   User
-   Role
-   Authentication
-   JWT
-   Authorization
-   Global error handling
-   API response standard

## Phase 2 -- Learning Content

Implement:

-   Level
-   Lesson
-   Vocabulary
-   Kanji
-   Grammar

Admin CRUD must be completed first.

## Phase 3 -- User Learning

Implement:

-   Vocabulary Flashcards
-   Vocabulary progress
-   Kanji learning
-   Grammar learning
-   Bookmark
-   Study session

## Phase 4 -- Exercises

Implement:

-   Exercise management
-   Multiple choice
-   True/False
-   Fill in blank
-   Exercise result

## Phase 5 -- JLPT

Implement:

-   Exam management
-   Exam attempt
-   Timer
-   Auto save
-   Auto submit
-   Result
-   History

## Phase 6 -- Advanced Learning

Implement:

-   Listening
-   Reading
-   Streak
-   Spaced repetition
-   Notifications
-   Dashboard statistics

## Phase 7 -- AI Features

Optional:

-   AI grammar explanation
-   AI Japanese conversation
-   AI writing correction
-   AI speaking practice
-   Personalized learning path
-   Weakness analysis

------------------------------------------------------------------------

# 39. Coding Rules for AI Agent

The coding agent MUST follow these rules.

## General

-   Do not rewrite existing functionality unless necessary.
-   Reuse existing components and services.
-   Avoid duplicated code.
-   Keep functions small and focused.
-   Use meaningful names.
-   Do not hard-code business values.
-   Use constants/enums for fixed values.

## Backend

-   Use DTOs.
-   Use Service layer.
-   Use Repository layer.
-   Use constructor injection.
-   Use Bean Validation.
-   Use transactions where required.
-   Do not expose Entity directly.
-   Do not put business logic in Controller.

## Frontend

-   Use TypeScript.
-   Avoid `any` unless absolutely necessary.
-   Centralize API requests.
-   Create reusable components.
-   Protect authenticated routes.
-   Protect admin routes.
-   Handle loading/error/empty states.

## Database

-   Use migrations.
-   Never manually modify production schema.
-   Add indexes where required.
-   Use foreign keys.
-   Use unique constraints for business uniqueness.

------------------------------------------------------------------------

# 40. AI Agent Workflow

Before implementing a feature, the coding agent should:

1.  Inspect the existing project structure.
2.  Inspect existing entities.
3.  Inspect existing APIs.
4.  Inspect existing frontend components.
5.  Reuse existing patterns.
6.  Identify affected files.
7.  Implement backend.
8.  Implement frontend.
9.  Add/update tests.
10. Run build.
11. Fix compilation errors.
12. Verify the feature against acceptance criteria.

The agent must not create duplicate implementations of an existing
feature.

------------------------------------------------------------------------

# 41. Testing

Backend tests should include:

-   Unit tests
-   Service tests
-   Controller tests
-   Repository tests where appropriate
-   Security tests

Important test cases:

``` text
Valid login
Invalid login
Duplicate username
Duplicate email
Unauthorized API
Forbidden admin API
Vocabulary CRUD
Vocabulary learning state
Exercise submission
Exam submission
Exam expiration
Bookmark duplication
User data isolation
```

Frontend tests should cover critical user flows:

``` text
Login
Register
Vocabulary learning
Exercise submission
Exam submission
Admin CRUD
```

------------------------------------------------------------------------

# 42. Definition of Done

A feature is considered complete only when:

-   Backend implementation is complete.
-   Frontend implementation is complete.
-   Database changes are included.
-   Validation is implemented.
-   Authorization is implemented.
-   Error handling is implemented.
-   Tests are added.
-   API response follows the standard format.
-   No compilation errors exist.
-   No TypeScript errors exist.
-   Existing functionality is not broken.
-   Acceptance criteria are satisfied.

------------------------------------------------------------------------

# 43. Initial MVP Scope

The first release should contain only:

### Authentication

-   Register
-   Login
-   JWT
-   Refresh Token
-   Logout
-   Role-based authorization

### Admin

-   User management
-   Level management
-   Lesson management
-   Vocabulary management
-   Kanji management
-   Grammar management
-   Exercise management

### User

-   Dashboard
-   Lesson list
-   Vocabulary learning
-   Kanji learning
-   Grammar learning
-   Exercises
-   Progress
-   Bookmark
-   Profile

JLPT exams, listening, reading, streak and advanced spaced repetition
can be implemented after the MVP.

------------------------------------------------------------------------

# 44. Important Implementation Principle

The application should be built incrementally.

Do not attempt to implement every feature at once.

Recommended order:

``` text
Authentication
      ↓
Database + Entities
      ↓
Admin CRUD
      ↓
User Learning
      ↓
Progress
      ↓
Exercises
      ↓
JLPT
      ↓
Listening / Reading
      ↓
Spaced Repetition
      ↓
AI Features
```

Each phase must be working before moving to the next phase.
