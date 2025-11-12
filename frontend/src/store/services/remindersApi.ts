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
    getReminders: builder.query<ReminderLog[], { eventId?: number }>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        if (params.eventId !== undefined) searchParams.append("eventId", params.eventId.toString());

        const queryString = searchParams.toString();
        return queryString ? `/reminders/logs?${queryString}` : '/reminders/logs';
      },
      providesTags: ["Reminders"],
    }),
    getReminderLogs: builder.query<ReminderLog[], { eventId?: number }>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        if (params.eventId !== undefined) searchParams.append("eventId", params.eventId.toString());

        const queryString = searchParams.toString();
        return queryString ? `/reminders/logs?${queryString}` : '/reminders/logs';
      },
      providesTags: ["ReminderLogs"],
    }),
    getGlobalSettings: builder.query<GlobalReminderSettings, void>({
      query: () => "/users/reminder-settings",
      providesTags: ["ReminderSettings"],
    }),
    updateGlobalSettings: builder.mutation<GlobalReminderSettings, UpdateGlobalReminderSettingsRequest>({
      query: (data) => ({
        url: "/users/reminder-settings",
        method: "PUT",
        body: data,
      }),
      invalidatesTags: ["ReminderSettings"],
    }),
    retryReminder: builder.mutation<void, number>({
      query: (logId) => ({
        url: `/reminders/retry/${logId}`,
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
