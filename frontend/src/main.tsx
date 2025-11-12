import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { Provider } from "react-redux";
import { RouterProvider } from "react-router-dom";
import { store } from "./store";
import { router } from "./routes";
import { ThemeProvider } from "./contexts/ThemeContext";
import { registerServiceWorker } from "./utils/registerServiceWorker";
import "./index.css";

// Service Worker 등록 (프로덕션 환경에서만)
if (import.meta.env.PROD) {
  registerServiceWorker();
}

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Provider store={store}>
      <ThemeProvider>
        <RouterProvider router={router} />
      </ThemeProvider>
    </Provider>
  </StrictMode>
);
