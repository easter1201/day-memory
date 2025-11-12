import { useState, useEffect } from "react";
import { PageLayout } from "../components/layout/PageLayout";
import { SettingsTabs } from "../components/settings/SettingsTabs";
import { Button } from "../components/ui/Button";
import { Switch } from "../components/ui/Switch";
import { Label } from "../components/ui/Label";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import {
  useGetNotificationSettingsQuery,
  useUpdateNotificationSettingsMutation,
} from "../store/services/usersApi";
import Toast from "../components/common/Toast";

const TIME_OPTIONS = [
  { value: "09:00", label: "오전 9시" },
  { value: "10:00", label: "오전 10시" },
  { value: "11:00", label: "오전 11시" },
  { value: "12:00", label: "오후 12시" },
  { value: "13:00", label: "오후 1시" },
  { value: "14:00", label: "오후 2시" },
  { value: "15:00", label: "오후 3시" },
  { value: "16:00", label: "오후 4시" },
  { value: "17:00", label: "오후 5시" },
  { value: "18:00", label: "오후 6시" },
  { value: "19:00", label: "오후 7시" },
  { value: "20:00", label: "오후 8시" },
];

export const NotificationSettingsPage = () => {
  const { data: settings, isLoading } = useGetNotificationSettingsQuery();
  const [updateSettings, { isLoading: isUpdating }] = useUpdateNotificationSettingsMutation();

  const [emailEnabled, setEmailEnabled] = useState(false);
  const [reminderTime, setReminderTime] = useState("09:00");

  useEffect(() => {
    if (settings) {
      setEmailEnabled(settings.emailNotificationsEnabled);
      setReminderTime(settings.reminderTime);
    }
  }, [settings]);

  const handleSave = async () => {
    try {
      await updateSettings({
        emailNotificationsEnabled: emailEnabled,
        reminderTime,
      }).unwrap();
      Toast.success("알림 설정이 저장되었습니다");
    } catch (error) {
      console.error("Failed to update notification settings:", error);
      Toast.error("알림 설정 저장에 실패했습니다");
    }
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <LoadingSpinner size="lg" text="설정을 불러오는 중..." />
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-4xl space-y-6">
        {/* Header */}
        <div>
          <h1 className="text-3xl font-bold">설정</h1>
          <p className="mt-1 text-muted-foreground">
            계정 정보를 관리하고 설정을 변경하세요
          </p>
        </div>

        {/* Tabs */}
        <SettingsTabs />

        {/* Notification Settings */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">알림 설정</h2>

          <div className="space-y-6">
            {/* Email Notifications Toggle */}
            <div className="flex items-center justify-between">
              <div className="space-y-0.5">
                <Label>이메일 알림</Label>
                <p className="text-sm text-muted-foreground">
                  이벤트 및 리마인더 알림을 이메일로 받습니다
                </p>
              </div>
              <Switch
                checked={emailEnabled}
                onCheckedChange={setEmailEnabled}
              />
            </div>

            {/* Reminder Send Time */}
            <div>
              <Label htmlFor="reminderTime">리마인더 발송 시간</Label>
              <p className="mb-2 text-sm text-muted-foreground">
                매일 설정한 시간에 리마인더가 발송됩니다
              </p>
              <div className="grid grid-cols-3 gap-2 sm:grid-cols-4 md:grid-cols-6">
                {TIME_OPTIONS.map((option) => (
                  <button
                    key={option.value}
                    type="button"
                    onClick={() => setReminderTime(option.value)}
                    className={`rounded-md border px-3 py-2 text-sm font-medium transition-colors ${
                      reminderTime === option.value
                        ? "border-primary bg-primary text-primary-foreground"
                        : "border-input bg-background hover:bg-accent"
                    }`}
                  >
                    {option.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Save Button */}
            <div className="flex justify-end border-t pt-4">
              <Button onClick={handleSave} disabled={isUpdating}>
                {isUpdating ? "저장 중..." : "저장"}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </PageLayout>
  );
};
