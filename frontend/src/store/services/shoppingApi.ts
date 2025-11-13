import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import type { ShoppingProduct, ShoppingSearchParams } from "../../types/shopping";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

export const shoppingApi = createApi({
  reducerPath: "shoppingApi",
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
  tagTypes: ["Shopping"],
  endpoints: (builder) => ({
    searchProducts: builder.query<ShoppingProduct[], ShoppingSearchParams>({
      query: (params) => {
        const searchParams = new URLSearchParams();
        searchParams.append("query", params.query);

        if (params.minPrice !== undefined) {
          searchParams.append("minPrice", params.minPrice.toString());
        }
        if (params.maxPrice !== undefined) {
          searchParams.append("maxPrice", params.maxPrice.toString());
        }
        if (params.display !== undefined) {
          searchParams.append("display", params.display.toString());
        }

        return `/shopping/search?${searchParams.toString()}`;
      },
      providesTags: ["Shopping"],
    }),
  }),
});

export const { useSearchProductsQuery, useLazySearchProductsQuery } = shoppingApi;
