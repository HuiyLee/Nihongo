/** Requirements section 14.1. All six types the backend data model supports. */
export type ExerciseType =
  'MULTIPLE_CHOICE' | 'MULTIPLE_ANSWER' | 'TRUE_FALSE' | 'FILL_IN_BLANK' | 'MATCHING' | 'LISTENING';

export const EXERCISE_TYPE_OPTIONS: { label: string; value: ExerciseType }[] = [
  { label: 'Multiple choice', value: 'MULTIPLE_CHOICE' },
  { label: 'Multiple answer', value: 'MULTIPLE_ANSWER' },
  { label: 'True / False', value: 'TRUE_FALSE' },
  { label: 'Fill in the blank', value: 'FILL_IN_BLANK' },
  { label: 'Matching', value: 'MATCHING' },
  { label: 'Listening', value: 'LISTENING' },
];

/** Types with a "select the right option(s)" submit UI - what this phase's attempt page renders. */
export const SELECTABLE_EXERCISE_TYPES: ExerciseType[] = [
  'MULTIPLE_CHOICE',
  'MULTIPLE_ANSWER',
  'TRUE_FALSE',
  'FILL_IN_BLANK',
];

export type ExerciseDifficulty = 'EASY' | 'MEDIUM' | 'HARD';

export const EXERCISE_DIFFICULTY_OPTIONS: { label: string; value: ExerciseDifficulty }[] = [
  { label: 'Easy', value: 'EASY' },
  { label: 'Medium', value: 'MEDIUM' },
  { label: 'Hard', value: 'HARD' },
];

export const EXERCISE_DIFFICULTY_COLOR: Record<ExerciseDifficulty, string> = {
  EASY: 'green',
  MEDIUM: 'gold',
  HARD: 'red',
};

/** Public/learner view of an answer option - never carries isCorrect. */
export interface ExerciseAnswer {
  id: number;
  answerText: string;
  orderIndex: number;
}

/** Admin view of an answer option - includes isCorrect for review/editing. */
export interface AdminExerciseAnswer extends ExerciseAnswer {
  correct: boolean;
}

interface ExerciseBase {
  id: number;
  lessonId?: number;
  lessonTitle?: string;
  levelId: number;
  levelCode: string;
  type: ExerciseType;
  question: string;
  explanation?: string;
  audioUrl?: string;
  imageUrl?: string;
  difficulty: ExerciseDifficulty;
  createdAt: string;
  updatedAt: string;
}

export interface Exercise extends ExerciseBase {
  answers: ExerciseAnswer[];
}

export interface AdminExercise extends ExerciseBase {
  answers: AdminExerciseAnswer[];
}

export interface ExerciseAnswerRequest {
  answerText: string;
  correct: boolean;
  orderIndex: number;
}

export interface ExerciseRequest {
  lessonId?: number;
  levelId: number;
  type: ExerciseType;
  question: string;
  explanation?: string;
  audioUrl?: string;
  imageUrl?: string;
  difficulty: ExerciseDifficulty;
  answers: ExerciseAnswerRequest[];
}

/** Requirements section 14.4. */
export interface SubmitExerciseRequest {
  answerIds: number[];
}

export interface SubmitExerciseResponse {
  correct: boolean;
  score: number;
  explanation?: string;
}
