import { ReactElement } from "react";
import { render, RenderOptions } from "@testing-library/react";
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import { configureStore } from "@reduxjs/toolkit";
import authReducer from "../store/slices/authSlice";
import eventsReducer from "../store/slices/eventsSlice";
import giftsReducer from "../store/slices/giftsSlice";
import uiReducer from "../store/slices/uiSlice";
import { eventsApi } from "../store/services/eventsApi";
import { giftsApi } from "../store/services/giftsApi";
import { authApi } from "../store/services/authApi";

interface ExtendedRenderOptions extends Omit<RenderOptions, "wrapper"> {
  preloadedState?: any;
  store?: any;
}

export function renderWithProviders(
  ui: ReactElement,
  {
    preloadedState = {},
    store = configureStore({
      reducer: {
        auth: authReducer,
        events: eventsReducer,
        gifts: giftsReducer,
        ui: uiReducer,
        [eventsApi.reducerPath]: eventsApi.reducer,
        [giftsApi.reducerPath]: giftsApi.reducer,
        [authApi.reducerPath]: authApi.reducer,
      },
      preloadedState,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware().concat(
          eventsApi.middleware,
          giftsApi.middleware,
          authApi.middleware
        ),
    }),
    ...renderOptions
  }: ExtendedRenderOptions = {}
) {
  function Wrapper({ children }: { children: React.ReactNode }) {
    return (
      <Provider store={store}>
        <BrowserRouter>{children}</BrowserRouter>
      </Provider>
    );
  }

  return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}

export * from "@testing-library/react";
