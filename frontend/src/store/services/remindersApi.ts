import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  Reminder,
  RemindersResponse,
  RemindersQueryParams,
  ReminderLog,
  ReminderLogsResponse,
  ReminderLogsQueryParams,
  GlobalReminderSettings,
  UpdateGlobalReminderSettingsRequest,
} from "../../types/reminder";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const remindersApi = createApi({
  reducerPath: "remindersApi",
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
  tagTypes: ["Reminders", "ReminderLogs", "ReminderSettings"],
  endpoints: (builder) => ({
    getReminders: builder.query<RemindersResponse, RemindersQueryParams>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        if (params.page !== undefined) searchParams.append("page", params.page.toString());
        if (params.size !== undefined) searchParams.append("size", params.size.toString());
        if (params.status) searchParams.append("status", params.status);

        return `/reminders?${searchParams.toString()}`;
      },
      providesTags: ["Reminders"],
    }),
    getReminderLogs: builder.query<ReminderLogsResponse, ReminderLogsQueryParams>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        if (params.page !== undefined) searchParams.append("page", params.page.toString());
        if (params.size !== undefined) searchParams.append("size", params.size.toString());
        if (params.status) searchParams.append("status", params.status);

        return `/reminders/logs?${searchParams.toString()}`;
      },
      providesTags: ["ReminderLogs"],
    }),
    getGlobalSettings: builder.query<GlobalReminderSettings, void>({
      query: () => "/reminders/settings",
      providesTags: ["ReminderSettings"],
    }),
    updateGlobalSettings: builder.mutation<GlobalReminderSettings, UpdateGlobalReminderSettingsRequest>({
      query: (data) => ({
        url: "/reminders/settings",
        method: "PUT",
        body: data,
      }),
      invalidatesTags: ["ReminderSettings"],
    }),
    retryReminder: builder.mutation<void, number>({
      query: (logId) => ({
        url: `/reminders/logs/${logId}/retry`,
        method: "POST",
      }),
      invalidatesTags: ["ReminderLogs"],
    }),
    deleteReminder: builder.mutation<void, number>({
      query: (id) => ({
        url: `/reminders/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Reminders"],
    }),
  }),
});

export const {
  useGetRemindersQuery,
  useGetReminderLogsQuery,
  useGetGlobalSettingsQuery,
  useUpdateGlobalSettingsMutation,
  useRetryReminderMutation,
  useDeleteReminderMutation,
} = remindersApi;
