import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import { ProtectedRoute } from "./components/auth/ProtectedRoute";
import { LandingPage } from "./pages/LandingPage";
import { LoginPage } from "./pages/LoginPage";
import { SignupPage } from "./pages/SignupPage";
import { PasswordResetPage } from "./pages/PasswordResetPage";
import { DashboardPage } from "./pages/DashboardPage";
import { EventsListPage } from "./pages/EventsListPage";
import { EventCreatePage } from "./pages/EventCreatePage";
import { EventDetailPage } from "./pages/EventDetailPage";
import { EventEditPage } from "./pages/EventEditPage";
import { GiftListPage } from "./pages/GiftListPage";
import { GiftCreatePage } from "./pages/GiftCreatePage";
import { GiftDetailPage } from "./pages/GiftDetailPage";
import { GiftEditPage } from "./pages/GiftEditPage";
import { RecommendationsListPage } from "./pages/RecommendationsListPage";
import { RecommendationRequestPage } from "./pages/RecommendationRequestPage";
import { RecommendationResultPage } from "./pages/RecommendationResultPage";
import { RemindersPage } from "./pages/RemindersPage";
import { ReminderLogsPage } from "./pages/ReminderLogsPage";
import { CalendarPage } from "./pages/CalendarPage";
import { ProfileSettingsPage } from "./pages/ProfileSettingsPage";
import { NotificationSettingsPage } from "./pages/NotificationSettingsPage";
import { AccountSettingsPage } from "./pages/AccountSettingsPage";
import { NotFoundPage } from "./pages/NotFoundPage";

function App() {
  return (
    <BrowserRouter>
      <Toaster position="top-right" />
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/password-reset" element={<PasswordResetPage />} />

        {/* Protected Routes */}
        <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />

        {/* Events */}
        <Route path="/events" element={<ProtectedRoute><EventsListPage /></ProtectedRoute>} />
        <Route path="/events/new" element={<ProtectedRoute><EventCreatePage /></ProtectedRoute>} />
        <Route path="/events/:id" element={<ProtectedRoute><EventDetailPage /></ProtectedRoute>} />
        <Route path="/events/:id/edit" element={<ProtectedRoute><EventEditPage /></ProtectedRoute>} />

        {/* Gifts */}
        <Route path="/gifts" element={<ProtectedRoute><GiftListPage /></ProtectedRoute>} />
        <Route path="/gifts/new" element={<ProtectedRoute><GiftCreatePage /></ProtectedRoute>} />
        <Route path="/gifts/:id" element={<ProtectedRoute><GiftDetailPage /></ProtectedRoute>} />
        <Route path="/gifts/:id/edit" element={<ProtectedRoute><GiftEditPage /></ProtectedRoute>} />

        {/* Recommendations */}
        <Route path="/recommendations" element={<ProtectedRoute><RecommendationsListPage /></ProtectedRoute>} />
        <Route path="/recommendations/request" element={<ProtectedRoute><RecommendationRequestPage /></ProtectedRoute>} />
        <Route path="/recommendations/result" element={<ProtectedRoute><RecommendationResultPage /></ProtectedRoute>} />
        <Route path="/recommendations/:id" element={<ProtectedRoute><RecommendationResultPage /></ProtectedRoute>} />

        {/* Reminders */}
        <Route path="/reminders" element={<ProtectedRoute><RemindersPage /></ProtectedRoute>} />
        <Route path="/reminders/logs" element={<ProtectedRoute><ReminderLogsPage /></ProtectedRoute>} />

        {/* Calendar */}
        <Route path="/calendar" element={<ProtectedRoute><CalendarPage /></ProtectedRoute>} />

        {/* Settings */}
        <Route path="/settings" element={<Navigate to="/settings/profile" replace />} />
        <Route path="/settings/profile" element={<ProtectedRoute><ProfileSettingsPage /></ProtectedRoute>} />
        <Route path="/settings/notifications" element={<ProtectedRoute><NotificationSettingsPage /></ProtectedRoute>} />
        <Route path="/settings/account" element={<ProtectedRoute><AccountSettingsPage /></ProtectedRoute>} />

        {/* 404 */}
        <Route path="/404" element={<NotFoundPage />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
