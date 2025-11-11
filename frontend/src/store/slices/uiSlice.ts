import { createSlice, PayloadAction } from "@reduxjs/toolkit";

type Theme = "light" | "dark" | "system";

interface UiState {
  isSidebarOpen: boolean;
  theme: Theme;
}

const getInitialTheme = (): Theme => {
  const savedTheme = localStorage.getItem("theme") as Theme;
  if (savedTheme) {
    return savedTheme;
  }
  return "system";
};

const getInitialSidebarState = (): boolean => {
  const saved = localStorage.getItem("sidebarOpen");
  if (saved !== null) {
    return saved === "true";
  }
  // Default: open on desktop, closed on mobile
  return window.innerWidth >= 768;
};

const initialState: UiState = {
  isSidebarOpen: getInitialSidebarState(),
  theme: getInitialTheme(),
};

const uiSlice = createSlice({
  name: "ui",
  initialState,
  reducers: {
    toggleSidebar: (state) => {
      state.isSidebarOpen = !state.isSidebarOpen;
      localStorage.setItem("sidebarOpen", String(state.isSidebarOpen));
    },
    setSidebarOpen: (state, action: PayloadAction<boolean>) => {
      state.isSidebarOpen = action.payload;
      localStorage.setItem("sidebarOpen", String(action.payload));
    },
    setTheme: (state, action: PayloadAction<Theme>) => {
      state.theme = action.payload;
      localStorage.setItem("theme", action.payload);

      // Apply theme to document
      if (action.payload === "dark") {
        document.documentElement.classList.add("dark");
      } else if (action.payload === "light") {
        document.documentElement.classList.remove("dark");
      } else {
        // System preference
        const isDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
        if (isDark) {
          document.documentElement.classList.add("dark");
        } else {
          document.documentElement.classList.remove("dark");
        }
      }
    },
    toggleTheme: (state) => {
      const newTheme: Theme = state.theme === "light" ? "dark" : "light";
      state.theme = newTheme;
      localStorage.setItem("theme", newTheme);

      if (newTheme === "dark") {
        document.documentElement.classList.add("dark");
      } else {
        document.documentElement.classList.remove("dark");
      }
    },
  },
});

export const { toggleSidebar, setSidebarOpen, setTheme, toggleTheme } = uiSlice.actions;
export default uiSlice.reducer;
