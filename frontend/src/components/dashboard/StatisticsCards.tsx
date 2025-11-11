import type { DashboardStatistics } from "../../types/dashboard";

interface StatisticsCardsProps {
  statistics: DashboardStatistics;
}

export const StatisticsCards = ({ statistics }: StatisticsCardsProps) => {
  const stats = [
    { label: "총 이벤트", value: statistics.totalEvents, color: "bg-blue-500" },
    { label: "총 선물", value: statistics.totalGifts, color: "bg-green-500" },
    { label: "이번 달 이벤트", value: statistics.thisMonthEvents, color: "bg-purple-500" },
  ];

  return (
    <div className="grid gap-4 md:grid-cols-3">
      {stats.map((stat) => (
        <div key={stat.label} className="rounded-lg border bg-card p-6 shadow-sm">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-muted-foreground">{stat.label}</p>
              <p className="mt-2 text-3xl font-bold">{stat.value}</p>
            </div>
            <div className={`h-12 w-12 rounded-full ${stat.color} opacity-10`} />
          </div>
        </div>
      ))}
    </div>
  );
};
