import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type { Event, EventsResponse, EventsQueryParams, CreateEventRequest, UpdateEventRequest } from "../../types/event";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const eventsApi = createApi({
  reducerPath: "eventsApi",
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
  tagTypes: ["Events"],
  endpoints: (builder) => ({
    getEvents: builder.query<EventsResponse, EventsQueryParams>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        if (params.page !== undefined) searchParams.append("page", params.page.toString());
        if (params.size !== undefined) searchParams.append("size", params.size.toString());
        if (params.filter && params.filter !== "all") searchParams.append("filter", params.filter);
        if (params.search) searchParams.append("search", params.search);

        return `/events?${searchParams.toString()}`;
      },
      transformResponse: (response: Event[], meta, arg) => {
        // Backend returns array, but frontend expects paginated response
        const pageSize = arg.size || 12;
        const currentPage = arg.page || 0;

        return {
          content: response,
          totalElements: response.length,
          totalPages: Math.ceil(response.length / pageSize),
          currentPage: currentPage,
          pageSize: pageSize,
        };
      },
      providesTags: ["Events"],
    }),
    getEventById: builder.query<Event, number>({
      query: (id) => `/events/${id}`,
      providesTags: (result, error, id) => [{ type: "Events", id }],
    }),
    createEvent: builder.mutation<Event, CreateEventRequest>({
      query: (newEvent) => ({
        url: "/events",
        method: "POST",
        body: newEvent,
      }),
      invalidatesTags: ["Events"],
    }),
    deleteEvent: builder.mutation<void, number>({
      query: (id) => ({
        url: `/events/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Events"],
    }),
    updateEvent: builder.mutation<Event, { id: number; data: UpdateEventRequest }>({
      query: ({ id, data }) => ({
        url: `/events/${id}`,
        method: "PUT",
        body: data,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "Events", id }, "Events"],
    }),
    getEventsByMonth: builder.query<Event[], { year: number; month: number }>({
      query: ({ year, month }) => `/statistics/calendar?year=${year}&month=${month}`,
      transformResponse: (response: any[]) => {
        // Backend returns CalendarEvent[], transform to Event[]
        return response.map((calEvent) => ({
          id: calEvent.eventId,
          title: calEvent.title,
          description: "",
          eventDate: calEvent.date,
          eventType: calEvent.type,
          isRecurring: false,
          isActive: true,
          isTracking: calEvent.tracking,
          dDay: calEvent.daysRemaining,
          reminders: [],
        }));
      },
      providesTags: ["Events"],
    }),
    toggleTracking: builder.mutation<Event, { id: number; isTracking: boolean }>({
      query: ({ id, isTracking }) => ({
        url: `/events/${id}/tracking`,
        method: "PUT",
        body: { isTracking },
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "Events", id }, "Events"],
    }),
  }),
});

export const { useGetEventsQuery, useGetEventByIdQuery, useCreateEventMutation, useDeleteEventMutation, useUpdateEventMutation, useGetEventsByMonthQuery, useToggleTrackingMutation } = eventsApi;
