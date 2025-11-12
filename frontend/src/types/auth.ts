export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface User {
  id: number;
  email: string;
  name: string;
}

export interface SignupRequest {
  email: string;
  password: string;
  nickname: string;
}

export interface SignupResponse {
  id: number;
  email: string;
  nickname: string;
  createdAt: string;
}

export interface PasswordResetRequest {
  email: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  accessToken: string;
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
}
