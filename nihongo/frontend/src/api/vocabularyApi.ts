import { createContentApi } from './contentApiFactory';
import type { Vocabulary, VocabularyRequest } from '../types/content';

export const vocabularyApi = createContentApi<Vocabulary, VocabularyRequest>(
  '/admin/vocabularies',
  '/vocabularies'
);
