import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./slices/authSlice";
import eventsReducer from "./slices/eventsSlice";
import giftsReducer from "./slices/giftsSlice";
import uiReducer from "./slices/uiSlice";
import { authApi } from "./services/authApi";
import { dashboardApi } from "./services/dashboardApi";
import { eventsApi } from "./services/eventsApi";
import { giftsApi } from "./services/giftsApi";
import { recommendationsApi } from "./services/recommendationsApi";
import { remindersApi } from "./services/remindersApi";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    events: eventsReducer,
    gifts: giftsReducer,
    ui: uiReducer,
    [authApi.reducerPath]: authApi.reducer,
    [dashboardApi.reducerPath]: dashboardApi.reducer,
    [eventsApi.reducerPath]: eventsApi.reducer,
    [giftsApi.reducerPath]: giftsApi.reducer,
    [recommendationsApi.reducerPath]: recommendationsApi.reducer,
    [remindersApi.reducerPath]: remindersApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      authApi.middleware,
      dashboardApi.middleware,
      eventsApi.middleware,
      giftsApi.middleware,
      recommendationsApi.middleware,
      remindersApi.middleware
    ),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
