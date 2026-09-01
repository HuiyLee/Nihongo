import { apiClient } from './axiosClient';
import type { ApiResponse } from '../types/api';
import type { AdminStats, CategoryProgress, ProgressOverview } from '../types/progress';

/** Requirements section 20. Always scoped to the caller. */
export const progressApi = {
  overview() {
    return apiClient.get<ApiResponse<ProgressOverview>>('/progress');
  },
  vocabulary() {
    return apiClient.get<ApiResponse<CategoryProgress>>('/progress/vocabulary');
  },
  kanji() {
    return apiClient.get<ApiResponse<CategoryProgress>>('/progress/kanji');
  },
  grammar() {
    return apiClient.get<ApiResponse<CategoryProgress>>('/progress/grammar');
  },
  lessons() {
    return apiClient.get<ApiResponse<CategoryProgress>>('/progress/lessons');
  },
  exams() {
    return apiClient.get<ApiResponse<CategoryProgress>>('/progress/exams');
  },
};

/** Requirements section 35. Admin-only. */
export const adminStatsApi = {
  get() {
    return apiClient.get<ApiResponse<AdminStats>>('/admin/stats');
  },
};
