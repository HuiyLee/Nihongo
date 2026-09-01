import type { AuthUser } from '../types/auth';

const ACCESS_TOKEN_KEY = 'nihongo.accessToken';
const REFRESH_TOKEN_KEY = 'nihongo.refreshToken';
const USER_KEY = 'nihongo.user';

/**
 * Single place that owns reading/writing auth state to storage, so no other
 * module talks to localStorage directly (requirements section 28: centralize,
 * do not duplicate logic across files).
 */
export const tokenStorage = {
  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },
  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },
  getUser(): AuthUser | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as AuthUser;
    } catch {
      return null;
    }
  },
  setSession(accessToken: string, refreshToken: string, user: AuthUser): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },
  setAccessToken(accessToken: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  },
  clear(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },
};
