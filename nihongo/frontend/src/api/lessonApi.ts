import { createContentApi } from './contentApiFactory';
import type { Lesson, LessonRequest } from '../types/content';

export const lessonApi = createContentApi<Lesson, LessonRequest>('/admin/lessons', '/lessons');
