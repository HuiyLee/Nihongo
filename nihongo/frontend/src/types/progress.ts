/** Requirements section 20. `known`/`total` are raw counts, `percent` is the pre-rounded 0-100 value to display directly. */
export interface CategoryProgress {
  known: number;
  total: number;
  percent: number;
}

export interface ProgressOverview {
  vocabulary: CategoryProgress;
  kanji: CategoryProgress;
  grammar: CategoryProgress;
  lessons: CategoryProgress;
  exams: CategoryProgress;
}

/** Requirements section 35. passRate is a percentage (0-100). */
export interface AdminStats {
  totalUsers: number;
  totalLessons: number;
  totalVocabulary: number;
  totalKanji: number;
  totalGrammar: number;
  totalExercises: number;
  totalExams: number;
  totalStudySessions: number;
  totalExamAttempts: number;
  passRate: number;
}
