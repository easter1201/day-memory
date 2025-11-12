import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  LoginRequest,
  LoginResponse,
  SignupRequest,
  SignupResponse,
  PasswordResetRequest,
  RefreshTokenRequest,
  RefreshTokenResponse,
} from "../../types/auth";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const authApi = createApi({
  reducerPath: "authApi",
  baseQuery: fetchBaseQuery({
    baseUrl: API_BASE_URL,
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as any).auth?.accessToken;
      if (token) {
        headers.set("Authorization", `Bearer ${token}`);
      }
      return headers;
    },
  }),
  endpoints: (builder) => ({
    login: builder.mutation<LoginResponse, LoginRequest>({
      query: (credentials) => ({
        url: "/users/login",
        method: "POST",
        body: credentials,
      }),
    }),
    signup: builder.mutation<SignupResponse, SignupRequest>({
      query: (data) => ({
        url: "/users/signup",
        method: "POST",
        body: data,
      }),
    }),
    passwordReset: builder.mutation<void, PasswordResetRequest>({
      query: (data) => ({
        url: "/verification/password-reset",
        method: "POST",
        body: data,
      }),
    }),
    logout: builder.mutation<void, void>({
      query: () => ({
        url: "/users/logout",
        method: "POST",
      }),
    }),
    refreshToken: builder.mutation<RefreshTokenResponse, RefreshTokenRequest>({
      query: (data) => ({
        url: "/users/refresh",
        method: "POST",
        body: data,
      }),
    }),
  }),
});

export const { useLoginMutation, useSignupMutation, usePasswordResetMutation, useLogoutMutation, useRefreshTokenMutation } =
  authApi;
