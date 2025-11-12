import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { EmptyState } from "../components/common/EmptyState";
import { Pagination } from "../components/common/Pagination";
import {
  useGetReminderLogsQuery,
  useRetryReminderMutation,
} from "../store/services/remindersApi";
import { formatDate } from "../utils/dateUtils";
import Toast from "../components/common/Toast";

const STATUS_LABELS = {
  SUCCESS: "성공",
  FAILED: "실패",
};

const STATUS_COLORS = {
  SUCCESS: "bg-green-100 text-green-800",
  FAILED: "bg-red-100 text-red-800",
};

type FilterStatus = "ALL" | "SUCCESS" | "FAILED";

export const ReminderLogsPage = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [statusFilter, setStatusFilter] = useState<FilterStatus>("ALL");

  const { data: logs, isLoading } = useGetReminderLogsQuery({
    page,
    size: 10,
    status: statusFilter === "ALL" ? undefined : statusFilter,
  });

  const [retryReminder, { isLoading: isRetrying }] = useRetryReminderMutation();

  const handleRetry = async (logId: number) => {
    if (!window.confirm("이 리마인더를 재발송하시겠습니까?")) return;

    try {
      await retryReminder(logId).unwrap();
      Toast.success("리마인더가 재발송되었습니다");
    } catch (error) {
      console.error("Failed to retry reminder:", error);
      Toast.error("재발송에 실패했습니다");
    }
  };

  const handleFilterChange = (filter: FilterStatus) => {
    setStatusFilter(filter);
    setPage(0);
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <LoadingSpinner size="lg" text="발송 내역을 불러오는 중..." />
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
            <h1 className="text-3xl font-bold">리마인더 발송 내역</h1>
            <p className="mt-1 text-muted-foreground">
              발송된 리마인더의 상태를 확인하고 관리하세요
            </p>
          </div>
          <Button onClick={() => navigate("/reminders")} variant="outline">
            리마인더 설정
          </Button>
        </div>

        {/* Filter Buttons */}
        <div className="flex gap-2">
          <button
            onClick={() => handleFilterChange("ALL")}
            className={`rounded-md border px-4 py-2 text-sm font-medium transition-colors ${
              statusFilter === "ALL"
                ? "border-primary bg-primary text-primary-foreground"
                : "border-input bg-background hover:bg-accent"
            }`}
          >
            전체
          </button>
          <button
            onClick={() => handleFilterChange("SUCCESS")}
            className={`rounded-md border px-4 py-2 text-sm font-medium transition-colors ${
              statusFilter === "SUCCESS"
                ? "border-primary bg-primary text-primary-foreground"
                : "border-input bg-background hover:bg-accent"
            }`}
          >
            성공
          </button>
          <button
            onClick={() => handleFilterChange("FAILED")}
            className={`rounded-md border px-4 py-2 text-sm font-medium transition-colors ${
              statusFilter === "FAILED"
                ? "border-primary bg-primary text-primary-foreground"
                : "border-input bg-background hover:bg-accent"
            }`}
          >
            실패
          </button>
        </div>

        {/* Logs Table */}
        <div className="rounded-lg border bg-card shadow-sm">
          {!logs || logs.content.length === 0 ? (
            <div className="p-6">
              <EmptyState
                title="발송 내역이 없습니다"
                description={
                  statusFilter === "ALL"
                    ? "아직 발송된 리마인더가 없습니다"
                    : `${STATUS_LABELS[statusFilter as "SUCCESS" | "FAILED"]} 상태의 발송 내역이 없습니다`
                }
              />
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b bg-muted/50">
                      <th className="px-4 py-3 text-left text-sm font-semibold">
                        이벤트
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold">
                        수신자
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold">
                        이벤트 날짜
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold">
                        발송 일시
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold">
                        상태
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold">
                        재시도 횟수
                      </th>
                      <th className="px-4 py-3 text-left text-sm font-semibold">
                        작업
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y">
                    {logs.content.map((log) => (
                      <tr key={log.id} className="hover:bg-muted/50">
                        <td className="px-4 py-3">
                          <div className="font-medium">{log.eventTitle}</div>
                        </td>
                        <td className="px-4 py-3">
                          <div className="text-sm">{log.recipientName}</div>
                        </td>
                        <td className="px-4 py-3">
                          <div className="text-sm">
                            {formatDate(log.eventDate)}
                          </div>
                        </td>
                        <td className="px-4 py-3">
                          <div className="text-sm">
                            {formatDate(log.sentAt)}
                          </div>
                        </td>
                        <td className="px-4 py-3">
                          <Badge className={`text-xs ${STATUS_COLORS[log.status]}`}>
                            {STATUS_LABELS[log.status]}
                          </Badge>
                          {log.errorMessage && (
                            <div className="mt-1 text-xs text-red-600">
                              {log.errorMessage}
                            </div>
                          )}
                        </td>
                        <td className="px-4 py-3">
                          <div className="text-sm">{log.retryCount}회</div>
                        </td>
                        <td className="px-4 py-3">
                          {log.status === "FAILED" && (
                            <Button
                              variant="outline"
                              size="sm"
                              onClick={() => handleRetry(log.id)}
                              disabled={isRetrying}
                            >
                              재발송
                            </Button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {logs.totalPages > 1 && (
                <div className="border-t p-4">
                  <Pagination
                    currentPage={page + 1}
                    totalPages={logs.totalPages}
                    onPageChange={(newPage) => setPage(newPage - 1)}
                  />
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </PageLayout>
  );
};
