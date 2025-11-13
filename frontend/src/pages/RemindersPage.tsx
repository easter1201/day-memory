import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Switch } from "../components/ui/Switch";
import { Badge } from "../components/ui/Badge";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { EmptyState } from "../components/common/EmptyState";
import {
  useGetReminderLogsQuery,
  useGetGlobalSettingsQuery,
  useUpdateGlobalSettingsMutation,
} from "../store/services/remindersApi";
import { REMINDER_OPTIONS } from "../constants";
import { formatDate } from "../utils/dateUtils";
import Toast from "../components/common/Toast";

const STATUS_LABELS = {
  SUCCESS: "발송 완료",
  FAILED: "발송 실패",
};

const STATUS_COLORS = {
  SUCCESS: "bg-green-100 text-green-800",
  FAILED: "bg-red-100 text-red-800",
};

export const RemindersPage = () => {
  const navigate = useNavigate();

  // 기본 설정값 (백엔드에 설정 기능이 구현되지 않은 경우 사용)
  const defaultSettings = {
    enabled: true,
    defaultDaysBefore: [1, 7],
    notificationMethod: "EMAIL" as const,
  };

  const [selectedDays, setSelectedDays] = useState<number[]>(defaultSettings.defaultDaysBefore);
  const [notificationMethod, setNotificationMethod] = useState<"EMAIL" | "SMS" | "BOTH">(defaultSettings.notificationMethod);

  const { data: reminders, isLoading: remindersLoading } = useGetReminderLogsQuery({});

  const { data: settings, isLoading: settingsLoading } = useGetGlobalSettingsQuery();
  const [updateSettings, { isLoading: isUpdating }] = useUpdateGlobalSettingsMutation();

  // 설정 로드 완료 시 초기값 설정
  useEffect(() => {
    if (settings) {
      setSelectedDays(settings.defaultDaysBefore);
      setNotificationMethod(settings.notificationMethod);
    }
  }, [settings]);

  const handleToggleDay = (day: number) => {
    if (selectedDays.includes(day)) {
      setSelectedDays(selectedDays.filter((d) => d !== day));
    } else {
      setSelectedDays([...selectedDays, day]);
    }
  };

  const handleSaveSettings = async () => {
    const settingsToUse = settings || defaultSettings;

    try {
      await updateSettings({
        enabled: settingsToUse.enabled,
        defaultDaysBefore: selectedDays,
        notificationMethod,
      }).unwrap();
      Toast.success("리마인더 설정이 저장되었습니다");
    } catch (error) {
      console.error("Failed to update settings:", error);
      Toast.error("설정 저장에 실패했습니다");
    }
  };

  const handleToggleEnabled = async (enabled: boolean) => {
    const settingsToUse = settings || defaultSettings;

    try {
      await updateSettings({
        enabled,
        defaultDaysBefore: settingsToUse.defaultDaysBefore,
        notificationMethod: settingsToUse.notificationMethod,
      }).unwrap();
      Toast.success(enabled ? "리마인더가 활성화되었습니다" : "리마인더가 비활성화되었습니다");
    } catch (error) {
      console.error("Failed to toggle reminder:", error);
      Toast.error("설정 변경에 실패했습니다");
    }
  };


  if (remindersLoading || settingsLoading) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <LoadingSpinner size="lg" text="리마인더를 불러오는 중..." />
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-6xl space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold">리마인더 설정</h1>
            <p className="mt-1 text-muted-foreground">
              이벤트 알림을 설정하고 관리하세요
            </p>
          </div>
          <Button onClick={() => navigate("/reminders/logs")} variant="outline">
            발송 내역 보기
          </Button>
        </div>

        {/* Global Settings */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">전역 리마인더 설정</h2>

          <div className="space-y-6">
            {/* Enable/Disable */}
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-medium">리마인더 활성화</h3>
                <p className="text-sm text-muted-foreground">
                  모든 이벤트에 대한 리마인더를 활성화/비활성화합니다
                </p>
              </div>
              <Switch
                checked={(settings || defaultSettings).enabled}
                onCheckedChange={handleToggleEnabled}
              />
            </div>

              {/* Default Days Before */}
              <div>
                <h3 className="mb-2 font-medium">기본 알림 시점</h3>
                <p className="mb-3 text-sm text-muted-foreground">
                  새 이벤트 생성 시 기본적으로 설정될 알림 시점을 선택하세요
                </p>
                <div className="flex flex-wrap gap-2">
                  {REMINDER_OPTIONS.map((option) => (
                    <button
                      key={option.value}
                      type="button"
                      onClick={() => handleToggleDay(option.value)}
                      className={`rounded-md border px-4 py-2 text-sm font-medium transition-colors ${
                        selectedDays?.includes(option.value)
                          ? "border-primary bg-primary text-primary-foreground"
                          : "border-input bg-background hover:bg-accent"
                      }`}
                    >
                      {option.label}
                    </button>
                  ))}
                </div>
              </div>

              {/* Notification Method */}
              <div>
                <h3 className="mb-2 font-medium">알림 방법</h3>
                <div className="flex gap-2">
                  {[
                    { value: "EMAIL", label: "이메일" },
                    { value: "SMS", label: "SMS" },
                    { value: "BOTH", label: "이메일 + SMS" },
                  ].map((method) => (
                    <button
                      key={method.value}
                      type="button"
                      onClick={() => setNotificationMethod(method.value as any)}
                      className={`rounded-md border px-4 py-2 text-sm font-medium transition-colors ${
                        notificationMethod === method.value
                          ? "border-primary bg-primary text-primary-foreground"
                          : "border-input bg-background hover:bg-accent"
                      }`}
                    >
                      {method.label}
                    </button>
                  ))}
                </div>
              </div>

            <Button onClick={handleSaveSettings} disabled={isUpdating}>
              {isUpdating ? "저장 중..." : "설정 저장"}
            </Button>
          </div>
        </div>

        {/* Recent Reminder Logs */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">최근 리마인더 발송 내역</h2>

          {!reminders || reminders.length === 0 ? (
            <EmptyState
              title="발송된 리마인더가 없습니다"
              description="이벤트를 생성하고 리마인더가 발송되면 여기에 표시됩니다"
            />
          ) : (
            <>
              <div className="space-y-3">
                {reminders.map((log) => (
                  <div
                    key={log.id}
                    className="flex items-center justify-between rounded-lg border p-4"
                  >
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <h3 className="font-semibold">{log.eventTitle}</h3>
                        <Badge className={`text-xs ${STATUS_COLORS[log.status]}`}>
                          {STATUS_LABELS[log.status]}
                        </Badge>
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {log.recipientName} - {formatDate(log.eventDate)}
                      </p>
                      <p className="mt-1 text-xs text-muted-foreground">
                        발송 시각: {formatDate(log.sentAt)}
                        {log.retryCount > 0 && ` (재시도: ${log.retryCount}회)`}
                      </p>
                      {log.errorMessage && (
                        <p className="mt-1 text-xs text-red-600">
                          오류: {log.errorMessage}
                        </p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </>
          )}
        </div>
      </div>
    </PageLayout>
  );
};
