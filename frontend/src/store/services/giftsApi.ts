import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type { Gift, GiftsResponse, GiftsQueryParams, CreateGiftRequest, UpdateGiftRequest } from "../../types/gift";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const giftsApi = createApi({
  reducerPath: "giftsApi",
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
  tagTypes: ["Gifts"],
  endpoints: (builder) => ({
    getGifts: builder.query<GiftsResponse, GiftsQueryParams>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        if (params.page !== undefined) searchParams.append("page", params.page.toString());
        if (params.size !== undefined) searchParams.append("size", params.size.toString());
        if (params.category) searchParams.append("category", params.category);
        if (params.isPurchased !== undefined) searchParams.append("isPurchased", params.isPurchased.toString());
        if (params.search) searchParams.append("search", params.search);

        return `/gifts?${searchParams.toString()}`;
      },
      providesTags: ["Gifts"],
    }),
    getGiftById: builder.query<Gift, number>({
      query: (id) => `/gifts/${id}`,
      providesTags: (result, error, id) => [{ type: "Gifts", id }],
    }),
    createGift: builder.mutation<Gift, CreateGiftRequest>({
      query: (newGift) => ({
        url: "/gifts",
        method: "POST",
        body: newGift,
      }),
      invalidatesTags: ["Gifts"],
    }),
    updateGift: builder.mutation<Gift, { id: number; data: UpdateGiftRequest }>({
      query: ({ id, data }) => ({
        url: `/gifts/${id}`,
        method: "PUT",
        body: data,
      }),
      invalidatesTags: (result, error, { id }) => [{ type: "Gifts", id }, "Gifts"],
    }),
    deleteGift: builder.mutation<void, number>({
      query: (id) => ({
        url: `/gifts/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Gifts"],
    }),
    togglePurchased: builder.mutation<Gift, number>({
      query: (id) => ({
        url: `/gifts/${id}/toggle-purchased`,
        method: "PATCH",
      }),
      invalidatesTags: (result, error, id) => [{ type: "Gifts", id }, "Gifts"],
    }),
  }),
});

export const {
  useGetGiftsQuery,
  useGetGiftByIdQuery,
  useCreateGiftMutation,
  useUpdateGiftMutation,
  useDeleteGiftMutation,
  useTogglePurchasedMutation,
} = giftsApi;
