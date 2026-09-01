import { apiClient } from './axiosClient';
import { createContentApi } from './contentApiFactory';
import type { ApiResponse } from '../types/api';
import type { Reading, ReadingRequest } from '../types/content';

const baseReadingApi = createContentApi<Reading, ReadingRequest>('/admin/readings', '/readings');

/** Adds the completion endpoint (section 16) on top of the standard CRUD/browse calls. */
export const readingApi = {
  ...baseReadingApi,
  complete(id: number) {
    return apiClient.post<ApiResponse<null>>(`/readings/${id}/complete`);
  },
};
