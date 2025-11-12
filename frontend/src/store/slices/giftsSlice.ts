import { createSlice } from "@reduxjs/toolkit";
import type { PayloadAction } from "@reduxjs/toolkit";

interface GiftsState {
  filter: {
    category: string;
    isPurchased?: boolean;
  };
  search: string;
  currentPage: number;
}

const initialState: GiftsState = {
  filter: {
    category: "",
    isPurchased: undefined,
  },
  search: "",
  currentPage: 0,
};

const giftsSlice = createSlice({
  name: "gifts",
  initialState,
  reducers: {
    setGiftCategoryFilter: (state, action: PayloadAction<string>) => {
      state.filter.category = action.payload;
      state.currentPage = 0; // Reset to first page when filter changes
    },
    setGiftPurchasedFilter: (state, action: PayloadAction<boolean | undefined>) => {
      state.filter.isPurchased = action.payload;
      state.currentPage = 0;
    },
    setGiftSearch: (state, action: PayloadAction<string>) => {
      state.search = action.payload;
      state.currentPage = 0;
    },
    setGiftPage: (state, action: PayloadAction<number>) => {
      state.currentPage = action.payload;
    },
    clearGiftFilters: (state) => {
      state.filter = {
        category: "",
        isPurchased: undefined,
      };
      state.search = "";
      state.currentPage = 0;
    },
  },
});

export const {
  setGiftCategoryFilter,
  setGiftPurchasedFilter,
  setGiftSearch,
  setGiftPage,
  clearGiftFilters,
} = giftsSlice.actions;

export default giftsSlice.reducer;
