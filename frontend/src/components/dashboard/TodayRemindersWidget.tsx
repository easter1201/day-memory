import { useNavigate } from "react-router-dom";
import type { TodayReminder } from "../../types/dashboard";

interface TodayRemindersWidgetProps {
  reminders: TodayReminder[];
}

export const TodayRemindersWidget = ({ reminders }: TodayRemindersWidgetProps) => {
  const navigate = useNavigate();

  if (!reminders || reminders.length === 0) {
    return null;
  }

  return (
    <div className="rounded-lg border bg-amber-50 dark:bg-amber-950 p-6 shadow-sm">
      <div className="flex items-center gap-2 mb-4">
        <svg
          className="h-5 w-5 text-amber-600 dark:text-amber-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
          />
        </svg>
        <h2 className="text-lg font-semibold text-amber-900 dark:text-amber-100">
          오늘의 리마인더
        </h2>
      </div>
      <div className="space-y-3">
        {reminders.map((reminder) => (
          <div
            key={reminder.eventId}
            onClick={() => navigate(`/events/${reminder.eventId}`)}
            className="flex cursor-pointer items-center justify-between rounded-lg border border-amber-200 dark:border-amber-800 bg-white dark:bg-amber-900 p-3 transition-colors hover:bg-amber-100 dark:hover:bg-amber-800"
          >
            <div>
              <p className="font-medium text-amber-900 dark:text-amber-100">
                {reminder.eventTitle}
              </p>
              <p className="text-sm text-amber-700 dark:text-amber-300">
                {reminder.recipientName} · {new Date(reminder.eventDate).toLocaleDateString()}
              </p>
            </div>
            <div className="flex-shrink-0">
              <span className="rounded-full bg-amber-600 dark:bg-amber-500 px-3 py-1 text-sm font-medium text-white">
                D-{reminder.daysUntilEvent}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
