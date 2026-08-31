import { apiClient } from './axiosClient';
import type { ApiResponse } from '../types/api';
import type { LearningProgress, MarkOutcome } from '../types/learning';

/**
 * Builds the /{id}/progress and /{id}/mark calls shared by Vocabulary,
 * Kanji, and Grammar (section 10), mirroring how contentApiFactory
 * centralizes the admin CRUD calls.
 */
function createLearningStateApi(publicBasePath: string) {
  return {
    getProgress(id: number) {
      return apiClient.get<ApiResponse<LearningProgress>>(`${publicBasePath}/${id}/progress`);
    },
    mark(id: number, outcome: MarkOutcome) {
      return apiClient.post<ApiResponse<LearningProgress>>(`${publicBasePath}/${id}/mark`, {
        outcome,
      });
    },
  };
}

export const vocabularyLearningApi = createLearningStateApi('/vocabularies');
export const kanjiLearningApi = createLearningStateApi('/kanji');
export const grammarLearningApi = createLearningStateApi('/grammars');
