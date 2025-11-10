import { configureStore } from '@reduxjs/toolkit';
import eventReducer from './slices/eventSlice';
import giftReducer from './slices/giftSlice';

export const store = configureStore({
  reducer: {
    events: eventReducer,
    gifts: giftReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
