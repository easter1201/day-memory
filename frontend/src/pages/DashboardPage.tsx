import { PageLayout } from "../components/layout/PageLayout";
import { WelcomeBanner } from "../components/dashboard/WelcomeBanner";
import { StatisticsCards } from "../components/dashboard/StatisticsCards";
import { UpcomingEventsWidget } from "../components/dashboard/UpcomingEventsWidget";
import { QuickActionButtons } from "../components/dashboard/QuickActionButtons";
import { TodayRemindersWidget } from "../components/dashboard/TodayRemindersWidget";
import { useGetDashboardDataQuery } from "../store/services/dashboardApi";

export const DashboardPage = () => {
  const { data, isLoading, error } = useGetDashboardDataQuery();

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-full items-center justify-center">
          <p className="text-muted-foreground">로딩 중...</p>
        </div>
      </PageLayout>
    );
  }

  if (error || !data) {
    return (
      <PageLayout>
        <div className="flex h-full items-center justify-center">
          <p className="text-red-500">데이터를 불러오는데 실패했습니다.</p>
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="space-y-6">
        <WelcomeBanner />

        <StatisticsCards data={data} />

        <TodayRemindersWidget reminders={data.todayReminders} />

        <div className="grid gap-6 lg:grid-cols-2">
          <UpcomingEventsWidget events={data.upcomingEvents} />

          <div className="rounded-lg border bg-card p-6 shadow-sm">
            <h2 className="text-lg font-semibold">빠른 작업</h2>
            <div className="mt-4">
              <QuickActionButtons />
            </div>
          </div>
        </div>
      </div>
    </PageLayout>
  );
};
