import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Switch } from "../components/ui/Switch";
import { Badge } from "../components/ui/Badge";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { EmptyState } from "../components/common/EmptyState";
import {
  useGetRemindersQuery,
  useGetGlobalSettingsQuery,
  useUpdateGlobalSettingsMutation,
  useDeleteReminderMutation,
} from "../store/services/remindersApi";
import { REMINDER_OPTIONS } from "../constants";
import { calculateDDay, formatDate } from "../utils/dateUtils";
import Toast from "../components/common/Toast";

const STATUS_LABELS = {
  PENDING: "대기 중",
  SENT: "발송 완료",
  FAILED: "발송 실패",
};

const STATUS_COLORS = {
  PENDING: "bg-yellow-100 text-yellow-800",
  SENT: "bg-green-100 text-green-800",
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

  const { data: reminders, isLoading: remindersLoading } = useGetRemindersQuery({});

  const { data: settings, isLoading: settingsLoading, error: settingsError } = useGetGlobalSettingsQuery();
  const [updateSettings, { isLoading: isUpdating }] = useUpdateGlobalSettingsMutation();
  const [deleteReminder] = useDeleteReminderMutation();

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

  const handleDelete = async (id: number) => {
    if (!window.confirm("이 리마인더를 삭제하시겠습니까?")) return;

    try {
      await deleteReminder(id).unwrap();
      Toast.success("리마인더가 삭제되었습니다");
    } catch (error) {
      console.error("Failed to delete reminder:", error);
      Toast.error("삭제에 실패했습니다");
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

        {/* Reminders List */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">예정된 리마인더</h2>

          {!reminders || reminders.length === 0 ? (
            <EmptyState
              title="예정된 리마인더가 없습니다"
              description="이벤트를 생성하면 자동으로 리마인더가 등록됩니다"
            />
          ) : (
            <>
              <div className="space-y-3">
                {reminders.map((reminder) => (
                  <div
                    key={reminder.id}
                    className="flex items-center justify-between rounded-lg border p-4"
                  >
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <h3 className="font-semibold">{reminder.eventTitle}</h3>
                        <Badge className={`text-xs ${STATUS_COLORS[reminder.status]}`}>
                          {STATUS_LABELS[reminder.status]}
                        </Badge>
                      </div>
                      <p className="text-sm text-muted-foreground">
                        {reminder.recipientName} - {formatDate(reminder.eventDate)}
                      </p>
                      <p className="mt-1 text-xs text-muted-foreground">
                        {reminder.daysBeforeEvent}일 전 알림 -{" "}
                        {formatDate(reminder.reminderDate)} ({calculateDDay(reminder.reminderDate)})
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleDelete(reminder.id)}
                    >
                      삭제
                    </Button>
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
