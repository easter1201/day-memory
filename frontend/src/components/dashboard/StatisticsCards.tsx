import type { DashboardData } from "../../types/dashboard";

interface StatisticsCardsProps {
  data: DashboardData;
}

export const StatisticsCards = ({ data }: StatisticsCardsProps) => {
  const stats = [
    { label: "다가오는 이벤트", value: data.upcomingEventsCount, color: "bg-blue-500" },
    { label: "미구매 선물", value: data.unpurchasedGiftsCount, color: "bg-green-500" },
    { label: "이번 달 이벤트", value: data.thisMonthEventsCount, color: "bg-purple-500" },
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
