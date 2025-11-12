import { createBrowserRouter, Navigate } from "react-router-dom";
import { PrivateRoute } from "../components/routes/PrivateRoute";
import { PublicRoute } from "../components/routes/PublicRoute";

// Layout
import { PageLayout } from "../components/layout/PageLayout";
import { AuthLayout } from "../components/layout/AuthLayout";

// Auth Pages
import { LoginPage } from "../pages/LoginPage";
import { SignupPage } from "../pages/SignupPage";
import { PasswordResetPage } from "../pages/PasswordResetPage";

// Dashboard
import { DashboardPage } from "../pages/DashboardPage";

// Events Pages
import { EventsListPage } from "../pages/EventsListPage";
import { EventCreatePage } from "../pages/EventCreatePage";
import { EventDetailPage } from "../pages/EventDetailPage";
import { EventEditPage } from "../pages/EventEditPage";

// Gifts Pages
import { GiftListPage } from "../pages/GiftListPage";
import { GiftCreatePage } from "../pages/GiftCreatePage";
import { GiftDetailPage } from "../pages/GiftDetailPage";
import { GiftEditPage } from "../pages/GiftEditPage";

// Error Pages
import { NotFoundPage } from "../pages/NotFoundPage";

export const router = createBrowserRouter([
  // Public Routes (인증된 사용자는 접근 불가)
  {
    element: <PublicRoute restricted />,
    children: [
      {
        element: <AuthLayout />,
        children: [
          {
            path: "/login",
            element: <LoginPage />,
          },
          {
            path: "/signup",
            element: <SignupPage />,
          },
          {
            path: "/password-reset",
            element: <PasswordResetPage />,
          },
        ],
      },
    ],
  },

  // Private Routes (인증 필요)
  {
    element: <PrivateRoute />,
    children: [
      // Root redirect
      {
        path: "/",
        element: <Navigate to="/dashboard" replace />,
      },

      // Dashboard
      {
        path: "/dashboard",
        element: <DashboardPage />,
      },

      // Events Routes
      {
        path: "/events",
        children: [
          {
            index: true,
            element: <EventsListPage />,
          },
          {
            path: "new",
            element: <EventCreatePage />,
          },
          {
            path: ":id",
            element: <EventDetailPage />,
          },
          {
            path: ":id/edit",
            element: <EventEditPage />,
          },
        ],
      },

      // Gifts Routes
      {
        path: "/gifts",
        children: [
          {
            index: true,
            element: <GiftListPage />,
          },
          {
            path: "new",
            element: <GiftCreatePage />,
          },
          {
            path: ":id",
            element: <GiftDetailPage />,
          },
          {
            path: ":id/edit",
            element: <GiftEditPage />,
          },
        ],
      },
    ],
  },

  // 404 Not Found (모든 라우트에서 매칭되지 않은 경우)
  {
    path: "*",
    element: <NotFoundPage />,
  },
]);
