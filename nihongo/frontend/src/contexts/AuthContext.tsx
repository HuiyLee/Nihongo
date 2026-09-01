import { createContext, useCallback, useState, type ReactNode } from 'react';
import { authApi } from '../api/authApi';
import { tokenStorage } from '../services/tokenStorage';
import type { AuthUser, LoginRequest, RegisterRequest } from '../types/auth';

export interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<void>;
  logout: () => Promise<void>;
}

// eslint-disable-next-line react-refresh/only-export-components
export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

/** Reads whatever session was persisted from a previous visit, synchronously, before first paint. */
function readStoredUser(): AuthUser | null {
  const storedUser = tokenStorage.getUser();
  const storedToken = tokenStorage.getAccessToken();
  return storedUser && storedToken ? storedUser : null;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(readStoredUser);

  const login = useCallback(async (payload: LoginRequest) => {
    const response = await authApi.login(payload);
    const { accessToken, refreshToken, user: loggedInUser } = response.data.data;
    tokenStorage.setSession(accessToken, refreshToken, loggedInUser);
    setUser(loggedInUser);
  }, []);

  const register = useCallback(async (payload: RegisterRequest) => {
    await authApi.register(payload);
  }, []);

  const logout = useCallback(async () => {
    const refreshToken = tokenStorage.getRefreshToken();
    try {
      if (refreshToken) {
        await authApi.logout(refreshToken);
      }
    } finally {
      tokenStorage.clear();
      setUser(null);
    }
  }, []);

  const value: AuthContextValue = {
    user,
    isAuthenticated: user !== null,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
