import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { tokenStorage } from '../services/tokenStorage';
import type { ApiResponse } from '../types/api';
import type { LoginResponseData } from '../types/auth';

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api';

const AUTH_FREE_PATHS = ['/auth/login', '/auth/register', '/auth/refresh'];

/** Centralized Axios instance - every API call in the app must go through this (never call axios directly from a page). */
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

/** Plain client used only for the refresh call itself, so it never recurses into the 401 handler below. */
const refreshClient = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const isAuthFree = AUTH_FREE_PATHS.some((path) => config.url?.includes(path));
  if (!isAuthFree) {
    const token = tokenStorage.getAccessToken();
    if (token) {
      config.headers.set('Authorization', `Bearer ${token}`);
    }
  }
  return config;
});

interface RetryableConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

let refreshPromise: Promise<string> | null = null;

async function refreshAccessToken(): Promise<string> {
  const refreshToken = tokenStorage.getRefreshToken();
  if (!refreshToken) {
    throw new Error('No refresh token available');
  }
  const response = await refreshClient.post<ApiResponse<LoginResponseData>>('/auth/refresh', {
    refreshToken,
  });
  const { accessToken, refreshToken: newRefreshToken, user } = response.data.data;
  tokenStorage.setSession(accessToken, newRefreshToken, user);
  return accessToken;
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetryableConfig | undefined;

    if (
      error.response?.status === 401 &&
      originalRequest &&
      !originalRequest._retry &&
      !AUTH_FREE_PATHS.some((path) => originalRequest.url?.includes(path))
    ) {
      originalRequest._retry = true;
      try {
        refreshPromise = refreshPromise ?? refreshAccessToken();
        const newAccessToken = await refreshPromise;
        refreshPromise = null;
        originalRequest.headers.set('Authorization', `Bearer ${newAccessToken}`);
        return apiClient(originalRequest);
      } catch (refreshError) {
        refreshPromise = null;
        tokenStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);
