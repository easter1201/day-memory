import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type {
  Recommendation,
  RecommendationsResponse,
  RecommendationsQueryParams,
  RecommendationRequest,
  SaveRecommendedGiftRequest,
} from "../../types/recommendation";
import type { Gift } from "../../types/gift";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const recommendationsApi = createApi({
  reducerPath: "recommendationsApi",
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
  tagTypes: ["Recommendations"],
  endpoints: (builder) => ({
    getRecommendations: builder.query<RecommendationsResponse, RecommendationsQueryParams>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        if (params.page !== undefined) searchParams.append("page", params.page.toString());
        if (params.size !== undefined) searchParams.append("size", params.size.toString());
        if (params.status) searchParams.append("status", params.status);

        return `/recommendations?${searchParams.toString()}`;
      },
      providesTags: ["Recommendations"],
    }),
    getRecommendationById: builder.query<Recommendation, number>({
      query: (id) => `/recommendations/${id}`,
      providesTags: (result, error, id) => [{ type: "Recommendations", id }],
    }),
    createRecommendation: builder.mutation<Recommendation, RecommendationRequest>({
      query: (data) => ({
        url: "/recommendations",
        method: "POST",
        body: data,
      }),
      invalidatesTags: ["Recommendations"],
    }),
    saveRecommendedGift: builder.mutation<Gift, SaveRecommendedGiftRequest>({
      query: (data) => ({
        url: `/recommendations/${data.recommendationId}/gifts/${data.recommendedGiftId}/save`,
        method: "POST",
      }),
      invalidatesTags: ["Recommendations"],
    }),
    deleteRecommendation: builder.mutation<void, number>({
      query: (id) => ({
        url: `/recommendations/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Recommendations"],
    }),
  }),
});

export const {
  useGetRecommendationsQuery,
  useGetRecommendationByIdQuery,
  useCreateRecommendationMutation,
  useSaveRecommendedGiftMutation,
  useDeleteRecommendationMutation,
} = recommendationsApi;
