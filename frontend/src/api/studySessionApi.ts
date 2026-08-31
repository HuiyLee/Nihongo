import { apiClient } from './axiosClient';
import type { ApiResponse, ListParams, PageResponse } from '../types/api';
import type { StudySessionRequest, StudySessionResponse } from '../types/learning';

/**
 * Requirements section 21. Not yet wired into any page - recording a
 * session (and the streak display it will feed, section 22) belongs to a
 * later phase. The API client exists now so that phase doesn't also have
 * to touch the request-building layer.
 */
export const studySessionApi = {
  record(payload: StudySessionRequest) {
    return apiClient.post<ApiResponse<StudySessionResponse>>('/study-sessions', payload);
  },
  list(params: ListParams) {
    return apiClient.get<ApiResponse<PageResponse<StudySessionResponse>>>('/study-sessions', {
      params,
    });
  },
};
