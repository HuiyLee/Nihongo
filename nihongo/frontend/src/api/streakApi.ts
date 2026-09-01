import { apiClient } from './axiosClient';
import type { ApiResponse } from '../types/api';
import type { Streak } from '../types/learning';

/** Requirements section 22. Always scoped to the caller. */
export const streakApi = {
  get() {
    return apiClient.get<ApiResponse<Streak>>('/streak');
  },
};
