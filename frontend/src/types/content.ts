export type ContentStatus = 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';

export const CONTENT_STATUS_OPTIONS: { label: string; value: ContentStatus }[] = [
  { label: 'Draft', value: 'DRAFT' },
  { label: 'Published', value: 'PUBLISHED' },
  { label: 'Archived', value: 'ARCHIVED' },
];

export interface Level {
  id: number;
  code: string;
  name: string;
  description?: string;
  orderIndex: number;
  status: ContentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface LevelRequest {
  code: string;
  name: string;
  description?: string;
  orderIndex: number;
  status: ContentStatus;
}

export interface Lesson {
  id: number;
  levelId: number;
  levelCode: string;
  title: string;
  description?: string;
  thumbnailUrl?: string;
  orderIndex: number;
  status: ContentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface LessonRequest {
  levelId: number;
  title: string;
  description?: string;
  thumbnailUrl?: string;
  orderIndex: number;
  status: ContentStatus;
}

interface LearningContentBase {
  id: number;
  lessonId?: number;
  lessonTitle?: string;
  levelId: number;
  levelCode: string;
  example?: string;
  exampleMeaning?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Vocabulary extends LearningContentBase {
  word: string;
  kanji?: string;
  hiragana?: string;
  katakana?: string;
  romaji?: string;
  meaning: string;
  partOfSpeech?: string;
  audioUrl?: string;
  imageUrl?: string;
}

export interface VocabularyRequest {
  lessonId?: number;
  levelId: number;
  word: string;
  kanji?: string;
  hiragana?: string;
  katakana?: string;
  romaji?: string;
  meaning: string;
  partOfSpeech?: string;
  example?: string;
  exampleMeaning?: string;
  audioUrl?: string;
  imageUrl?: string;
}

export interface Kanji extends LearningContentBase {
  character: string;
  meaning: string;
  onyomi?: string;
  kunyomi?: string;
  strokeCount?: number;
  strokeOrderImageUrl?: string;
  audioUrl?: string;
}

export interface KanjiRequest {
  lessonId?: number;
  levelId: number;
  character: string;
  meaning: string;
  onyomi?: string;
  kunyomi?: string;
  strokeCount?: number;
  strokeOrderImageUrl?: string;
  example?: string;
  exampleMeaning?: string;
  audioUrl?: string;
}

export interface Grammar extends LearningContentBase {
  pattern: string;
  meaning: string;
  formation?: string;
  explanation?: string;
  notes?: string;
}

export interface GrammarRequest {
  lessonId?: number;
  levelId: number;
  pattern: string;
  meaning: string;
  formation?: string;
  explanation?: string;
  example?: string;
  exampleMeaning?: string;
  notes?: string;
}
