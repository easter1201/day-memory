import { lazy, Suspense } from "react";
import { createBrowserRouter, Navigate } from "react-router-dom";
import { PrivateRoute } from "../components/routes/PrivateRoute";
import { PublicRoute } from "../components/routes/PublicRoute";
import { LoadingSpinner } from "../components/common/LoadingSpinner";

// Layout (not lazy loaded - needed immediately)
import { AuthLayout } from "../components/layout/AuthLayout";

// Landing Page (not lazy loaded - initial route)
import { LandingPage } from "../pages/LandingPage";

// Lazy loaded pages
// Auth Pages
const LoginPage = lazy(() => import("../pages/LoginPage").then(m => ({ default: m.LoginPage })));
const SignupPage = lazy(() => import("../pages/SignupPage").then(m => ({ default: m.SignupPage })));
const PasswordResetPage = lazy(() => import("../pages/PasswordResetPage").then(m => ({ default: m.PasswordResetPage })));

// Dashboard
const DashboardPage = lazy(() => import("../pages/DashboardPage").then(m => ({ default: m.DashboardPage })));

// Events Pages
const EventsListPage = lazy(() => import("../pages/EventsListPage").then(m => ({ default: m.EventsListPage })));
const EventCreatePage = lazy(() => import("../pages/EventCreatePage").then(m => ({ default: m.EventCreatePage })));
const EventDetailPage = lazy(() => import("../pages/EventDetailPage").then(m => ({ default: m.EventDetailPage })));
const EventEditPage = lazy(() => import("../pages/EventEditPage").then(m => ({ default: m.EventEditPage })));

// Gifts Pages
const GiftListPage = lazy(() => import("../pages/GiftListPage").then(m => ({ default: m.GiftListPage })));
const GiftCreatePage = lazy(() => import("../pages/GiftCreatePage").then(m => ({ default: m.GiftCreatePage })));
const GiftDetailPage = lazy(() => import("../pages/GiftDetailPage").then(m => ({ default: m.GiftDetailPage })));
const GiftEditPage = lazy(() => import("../pages/GiftEditPage").then(m => ({ default: m.GiftEditPage })));

// Recommendation Pages
const RecommendationsListPage = lazy(() => import("../pages/RecommendationsListPage").then(m => ({ default: m.RecommendationsListPage })));
const RecommendationRequestPage = lazy(() => import("../pages/RecommendationRequestPage").then(m => ({ default: m.RecommendationRequestPage })));
const RecommendationResultPage = lazy(() => import("../pages/RecommendationResultPage").then(m => ({ default: m.RecommendationResultPage })));

// Reminder Pages
const RemindersPage = lazy(() => import("../pages/RemindersPage").then(m => ({ default: m.RemindersPage })));
const ReminderLogsPage = lazy(() => import("../pages/ReminderLogsPage").then(m => ({ default: m.ReminderLogsPage })));

// Calendar Page
const CalendarPage = lazy(() => import("../pages/CalendarPage").then(m => ({ default: m.CalendarPage })));

// Settings Pages
const ProfileSettingsPage = lazy(() => import("../pages/ProfileSettingsPage").then(m => ({ default: m.ProfileSettingsPage })));
const NotificationSettingsPage = lazy(() => import("../pages/NotificationSettingsPage").then(m => ({ default: m.NotificationSettingsPage })));
const AccountSettingsPage = lazy(() => import("../pages/AccountSettingsPage").then(m => ({ default: m.AccountSettingsPage })));

// Error Pages
const NotFoundPage = lazy(() => import("../pages/NotFoundPage").then(m => ({ default: m.NotFoundPage })));

// Loading fallback component
const PageLoader = () => (
  <div className="flex h-screen items-center justify-center">
    <LoadingSpinner size="lg" text="페이지를 불러오는 중..." />
  </div>
);

// Wrapper component for Suspense
const SuspenseWrapper = ({ children }: { children: React.ReactNode }) => (
  <Suspense fallback={<PageLoader />}>{children}</Suspense>
);

export const router = createBrowserRouter([
  // Landing Page (Public, redirects authenticated users)
  {
    path: "/",
    element: <PublicRoute restricted redirectTo="/dashboard" />,
    children: [
      {
        index: true,
        element: <LandingPage />,
      },
    ],
  },

  // Auth Pages (Public, restricted for authenticated users)
  {
    element: <PublicRoute restricted redirectTo="/dashboard" />,
    children: [
      {
        element: <AuthLayout />,
        children: [
          {
            path: "/login",
            element: <SuspenseWrapper><LoginPage /></SuspenseWrapper>,
          },
          {
            path: "/signup",
            element: <SuspenseWrapper><SignupPage /></SuspenseWrapper>,
          },
          {
            path: "/password-reset",
            element: <SuspenseWrapper><PasswordResetPage /></SuspenseWrapper>,
          },
        ],
      },
    ],
  },

  // Private Routes (인증 필요)
  {
    element: <PrivateRoute />,
    children: [

      // Dashboard
      {
        path: "/dashboard",
        element: <SuspenseWrapper><DashboardPage /></SuspenseWrapper>,
      },

      // Events Routes
      {
        path: "/events",
        children: [
          {
            index: true,
            element: <SuspenseWrapper><EventsListPage /></SuspenseWrapper>,
          },
          {
            path: "new",
            element: <SuspenseWrapper><EventCreatePage /></SuspenseWrapper>,
          },
          {
            path: ":id",
            element: <SuspenseWrapper><EventDetailPage /></SuspenseWrapper>,
          },
          {
            path: ":id/edit",
            element: <SuspenseWrapper><EventEditPage /></SuspenseWrapper>,
          },
        ],
      },

      // Gifts Routes
      {
        path: "/gifts",
        children: [
          {
            index: true,
            element: <SuspenseWrapper><GiftListPage /></SuspenseWrapper>,
          },
          {
            path: "new",
            element: <SuspenseWrapper><GiftCreatePage /></SuspenseWrapper>,
          },
          {
            path: ":id",
            element: <SuspenseWrapper><GiftDetailPage /></SuspenseWrapper>,
          },
          {
            path: ":id/edit",
            element: <SuspenseWrapper><GiftEditPage /></SuspenseWrapper>,
          },
        ],
      },

      // Recommendations Routes
      {
        path: "/recommendations",
        children: [
          {
            index: true,
            element: <SuspenseWrapper><RecommendationsListPage /></SuspenseWrapper>,
          },
          {
            path: "new",
            element: <SuspenseWrapper><RecommendationRequestPage /></SuspenseWrapper>,
          },
          {
            path: ":id",
            element: <SuspenseWrapper><RecommendationResultPage /></SuspenseWrapper>,
          },
        ],
      },

      // Reminders Routes
      {
        path: "/reminders",
        children: [
          {
            index: true,
            element: <SuspenseWrapper><RemindersPage /></SuspenseWrapper>,
          },
          {
            path: "logs",
            element: <SuspenseWrapper><ReminderLogsPage /></SuspenseWrapper>,
          },
        ],
      },

      // Calendar Route
      {
        path: "/calendar",
        element: <SuspenseWrapper><CalendarPage /></SuspenseWrapper>,
      },

      // Settings Routes
      {
        path: "/settings",
        children: [
          {
            index: true,
            element: <Navigate to="/settings/profile" replace />,
          },
          {
            path: "profile",
            element: <SuspenseWrapper><ProfileSettingsPage /></SuspenseWrapper>,
          },
          {
            path: "notifications",
            element: <SuspenseWrapper><NotificationSettingsPage /></SuspenseWrapper>,
          },
          {
            path: "account",
            element: <SuspenseWrapper><AccountSettingsPage /></SuspenseWrapper>,
          },
        ],
      },
    ],
  },

  // 404 Not Found (모든 라우트에서 매칭되지 않은 경우)
  {
    path: "*",
    element: <SuspenseWrapper><NotFoundPage /></SuspenseWrapper>,
  },
]);
