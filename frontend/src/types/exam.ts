import type { ContentStatus } from './content';
import type { AdminExercise, Exercise } from './exercise';

/**
 * Requirements section 17.1. Public/learner view is flat - no nested
 * questions - so browsing a list of exams can never leak isCorrect; the
 * question list only appears once a learner actually starts the exam
 * (see ExamAttempt below).
 */
export interface Exam {
  id: number;
  levelId: number;
  levelCode: string;
  title: string;
  description?: string;
  durationMinutes: number;
  totalQuestions: number;
  status: ContentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface AdminExamQuestion {
  id: number;
  orderIndex: number;
  exercise: AdminExercise;
}

export interface AdminExam {
  id: number;
  levelId: number;
  levelCode: string;
  title: string;
  description?: string;
  durationMinutes: number;
  totalQuestions: number;
  status: ContentStatus;
  questions: AdminExamQuestion[];
  createdAt: string;
  updatedAt: string;
}

export interface ExamQuestionRequest {
  exerciseId: number;
  orderIndex: number;
}

/** totalQuestions is intentionally absent - the backend always computes it from questions.length. */
export interface ExamRequest {
  levelId: number;
  title: string;
  description?: string;
  durationMinutes: number;
  status: ContentStatus;
  questions: ExamQuestionRequest[];
}

/** One question as delivered to a learner mid-attempt - the masked Exercise shape, never isCorrect. */
export interface ExamQuestion {
  id: number;
  orderIndex: number;
  exercise: Exercise;
}

/** One question's previously auto-saved selection - see ExamAttempt.savedAnswers below. */
export interface SavedAnswer {
  examQuestionId: number;
  answerIds: number[];
}

/**
 * Requirements section 18. What POST /start returns - this is what delivers
 * the question list to the client. savedAnswers carries whatever PUT
 * /{id}/save last persisted (empty on a brand-new attempt), so resuming a
 * live attempt after a refresh can restore prior selections instead of
 * losing them (section 38 Phase 5: "Auto save").
 */
export interface ExamAttempt {
  attemptId: number;
  examId: number;
  examTitle: string;
  durationMinutes: number;
  startedAt: string;
  status: 'IN_PROGRESS' | 'COMPLETED' | 'EXPIRED';
  questions: ExamQuestion[];
  savedAnswers: SavedAnswer[];
}

export interface ExamAnswerSubmission {
  examQuestionId: number;
  answerIds: number[];
}

export interface SubmitExamRequest {
  answers: ExamAnswerSubmission[];
}

/** Requirements section 18-19. Shared by submit()'s response and GET /result. */
export interface ExamResult {
  attemptId: number;
  examId: number;
  examTitle: string;
  status: 'IN_PROGRESS' | 'COMPLETED' | 'EXPIRED';
  score: number;
  correctCount: number;
  wrongCount: number;
  totalQuestions: number;
  startedAt: string;
  submittedAt?: string;
}
