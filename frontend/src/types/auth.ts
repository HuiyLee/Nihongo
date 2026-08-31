export type UserRole = 'ROLE_ADMIN' | 'ROLE_USER';

export interface AuthUser {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: UserRole;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponseData {
  accessToken: string;
  refreshToken: string;
  user: AuthUser;
}
