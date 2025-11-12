import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  User,
  UpdateProfileRequest,
  NotificationSettings,
  UpdateNotificationSettingsRequest,
  ChangePasswordRequest,
} from "../../types/user";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const usersApi = createApi({
  reducerPath: "usersApi",
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
  tagTypes: ["User", "NotificationSettings"],
  endpoints: (builder) => ({
    getProfile: builder.query<User, void>({
      query: () => "/users/me",
      providesTags: ["User"],
    }),
    updateProfile: builder.mutation<User, UpdateProfileRequest>({
      query: (data) => ({
        url: "/users/me",
        method: "PUT",
        body: data,
      }),
      invalidatesTags: ["User"],
    }),
    getNotificationSettings: builder.query<NotificationSettings, void>({
      query: () => "/users/notification-settings",
      providesTags: ["NotificationSettings"],
      // 백엔드에 아직 구현되지 않은 엔드포인트이므로 기본값 반환
      transformErrorResponse: (response) => {
        return { emailNotificationsEnabled: true, reminderTime: "09:00" };
      },
    }),
    updateNotificationSettings: builder.mutation<NotificationSettings, UpdateNotificationSettingsRequest>({
      query: (data) => ({
        url: "/users/notification-settings",
        method: "PUT",
        body: data,
      }),
      invalidatesTags: ["NotificationSettings"],
      // 백엔드에 아직 구현되지 않았으므로 요청한 데이터를 그대로 반환
      transformResponse: (response, meta, arg) => arg,
    }),
    changePassword: builder.mutation<void, ChangePasswordRequest>({
      query: (data) => ({
        url: "/users/password",
        method: "PUT",
        body: data,
      }),
    }),
    deleteAccount: builder.mutation<void, void>({
      query: () => ({
        url: "/users/account",
        method: "DELETE",
      }),
    }),
  }),
});

export const {
  useGetProfileQuery,
  useUpdateProfileMutation,
  useGetNotificationSettingsQuery,
  useUpdateNotificationSettingsMutation,
  useChangePasswordMutation,
  useDeleteAccountMutation,
} = usersApi;
