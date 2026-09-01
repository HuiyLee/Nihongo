import { apiClient } from './axiosClient';
import type { ApiResponse, ListParams, PageResponse } from '../types/api';

/**
 * Builds the admin CRUD + public list/detail calls shared by every
 * paginated content resource (Lesson, Vocabulary, Kanji, Grammar) so the
 * request-building logic exists exactly once (section 28: centralize API
 * calls, do not duplicate).
 */
export function createContentApi<T, TRequest>(adminBasePath: string, publicBasePath: string) {
  return {
    listAdmin(params: ListParams) {
      return apiClient.get<ApiResponse<PageResponse<T>>>(adminBasePath, { params });
    },
    getAdmin(id: number) {
      return apiClient.get<ApiResponse<T>>(`${adminBasePath}/${id}`);
    },
    listPublic(params: ListParams) {
      return apiClient.get<ApiResponse<PageResponse<T>>>(publicBasePath, { params });
    },
    getPublic(id: number) {
      return apiClient.get<ApiResponse<T>>(`${publicBasePath}/${id}`);
    },
    create(payload: TRequest) {
      return apiClient.post<ApiResponse<T>>(adminBasePath, payload);
    },
    update(id: number, payload: TRequest) {
      return apiClient.put<ApiResponse<T>>(`${adminBasePath}/${id}`, payload);
    },
    remove(id: number) {
      return apiClient.delete<ApiResponse<null>>(`${adminBasePath}/${id}`);
    },
  };
}
