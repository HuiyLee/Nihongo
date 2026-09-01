import { createContentApi } from './contentApiFactory';
import type { Kanji, KanjiRequest } from '../types/content';

export const kanjiApi = createContentApi<Kanji, KanjiRequest>('/admin/kanji', '/kanji');
