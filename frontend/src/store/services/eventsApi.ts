import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type { Event, EventsResponse, EventsQueryParams } from "../../types/event";

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
      providesTags: ["Events"],
    }),
    getEventById: builder.query<Event, number>({
      query: (id) => `/events/${id}`,
      providesTags: (result, error, id) => [{ type: "Events", id }],
    }),
  }),
});

export const { useGetEventsQuery, useGetEventByIdQuery } = eventsApi;
