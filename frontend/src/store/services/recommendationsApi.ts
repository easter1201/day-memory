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
    getRecommendations: builder.query<Recommendation[], void>({
      query: () => `/ai/recommendations`,
      providesTags: ["Recommendations"],
    }),
    getRecommendationById: builder.query<Recommendation, number>({
      query: (id) => `/ai/recommendations/${id}`,
      providesTags: (result, error, id) => [{ type: "Recommendations", id }],
    }),
    createRecommendation: builder.mutation<Recommendation, RecommendationRequest>({
      query: (data) => ({
        url: "/ai/recommendations",
        method: "POST",
        body: data,
      }),
      invalidatesTags: ["Recommendations"],
    }),
    saveRecommendedGift: builder.mutation<Gift, SaveRecommendedGiftRequest>({
      query: (data) => ({
        url: `/ai/recommendations/${data.recommendationId}/gifts/${data.recommendedGiftId}/save`,
        method: "POST",
      }),
      invalidatesTags: ["Recommendations"],
    }),
    deleteRecommendation: builder.mutation<void, number>({
      query: (id) => ({
        url: `/ai/recommendations/${id}`,
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
