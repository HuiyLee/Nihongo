/** Requirements section 10 - per-user learning state on a Vocabulary/Kanji/Grammar item. */
export type LearningStatus = 'NEW' | 'LEARNING' | 'KNOWN' | 'REVIEW';

export type MarkOutcome = 'KNOWN' | 'UNKNOWN';

export interface LearningProgress {
  status: LearningStatus;
  correctCount: number;
  wrongCount: number;
  lastReviewedAt?: string;
  nextReviewAt?: string;
}

export const LEARNING_STATUS_LABEL: Record<LearningStatus, string> = {
  NEW: 'New',
  LEARNING: 'Learning',
  KNOWN: 'Known',
  REVIEW: 'Review',
};

export const LEARNING_STATUS_COLOR: Record<LearningStatus, string> = {
  NEW: 'default',
  LEARNING: 'gold',
  KNOWN: 'green',
  REVIEW: 'blue',
};

/** Requirements section 23. */
export type BookmarkTargetType = 'VOCABULARY' | 'KANJI' | 'GRAMMAR' | 'READING';

export interface Bookmark {
  id: number;
  targetType: BookmarkTargetType;
  targetId: number;
  displayText?: string;
  createdAt: string;
}

export interface BookmarkRequest {
  targetType: BookmarkTargetType;
  targetId: number;
}

/** Requirements section 21. */
export type StudyActivityType =
  'LESSON' | 'VOCABULARY' | 'KANJI' | 'GRAMMAR' | 'LISTENING' | 'READING' | 'EXAM';

export interface StudySessionRequest {
  activityType: StudyActivityType;
  referenceId?: number;
  startedAt: string;
  endedAt: string;
}

export interface StudySessionResponse {
  id: number;
  activityType: StudyActivityType;
  referenceId?: number;
  startedAt: string;
  endedAt: string;
  durationSeconds: number;
}
