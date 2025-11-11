import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface EventsState {
  filter: {
    eventType: string;
    isTracked?: boolean;
  };
  search: string;
  currentPage: number;
}

const initialState: EventsState = {
  filter: {
    eventType: "",
    isTracked: undefined,
  },
  search: "",
  currentPage: 0,
};

const eventsSlice = createSlice({
  name: "events",
  initialState,
  reducers: {
    setEventTypeFilter: (state, action: PayloadAction<string>) => {
      state.filter.eventType = action.payload;
      state.currentPage = 0; // Reset to first page when filter changes
    },
    setTrackedFilter: (state, action: PayloadAction<boolean | undefined>) => {
      state.filter.isTracked = action.payload;
      state.currentPage = 0;
    },
    setEventSearch: (state, action: PayloadAction<string>) => {
      state.search = action.payload;
      state.currentPage = 0;
    },
    setEventPage: (state, action: PayloadAction<number>) => {
      state.currentPage = action.payload;
    },
    clearEventFilters: (state) => {
      state.filter = {
        eventType: "",
        isTracked: undefined,
      };
      state.search = "";
      state.currentPage = 0;
    },
  },
});

export const {
  setEventTypeFilter,
  setTrackedFilter,
  setEventSearch,
  setEventPage,
  clearEventFilters,
} = eventsSlice.actions;

export default eventsSlice.reducer;
