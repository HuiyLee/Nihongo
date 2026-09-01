import { apiClient } from './axiosClient';
import type { ApiResponse } from '../types/api';
import type { AuthUser, LoginRequest, LoginResponseData, RegisterRequest } from '../types/auth';

/**
 * All /api/auth/* calls live here so pages/components never build requests
 * themselves (requirements section 28: "Do not duplicate API logic inside pages").
 */
export const authApi = {
  register(payload: RegisterRequest) {
    return apiClient.post<ApiResponse<AuthUser>>('/auth/register', payload);
  },
  login(payload: LoginRequest) {
    return apiClient.post<ApiResponse<LoginResponseData>>('/auth/login', payload);
  },
  refresh(refreshToken: string) {
    return apiClient.post<ApiResponse<LoginResponseData>>('/auth/refresh', { refreshToken });
  },
  logout(refreshToken: string) {
    return apiClient.post<ApiResponse<null>>('/auth/logout', { refreshToken });
  },
  me() {
    return apiClient.get<ApiResponse<AuthUser>>('/users/me');
  },
};
