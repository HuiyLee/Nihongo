import { createContentApi } from './contentApiFactory';
import type { Grammar, GrammarRequest } from '../types/content';

export const grammarApi = createContentApi<Grammar, GrammarRequest>('/admin/grammars', '/grammars');
