import type { Event } from "../../types/event";

interface ReminderSectionProps {
  event: Event;
}

const REMINDER_LABELS: Record<number, string> = {
  30: "30일 전",
  14: "14일 전",
  7: "7일 전",
  3: "3일 전",
  1: "1일 전",
};

export const ReminderSection = ({ event }: ReminderSectionProps) => {
  if (!event.reminders || event.reminders.length === 0) {
    return (
      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <h2 className="text-lg font-semibold">리마인더</h2>
        <p className="mt-4 text-sm text-muted-foreground">설정된 리마인더가 없습니다.</p>
      </div>
    );
  }

  const sortedReminders = [...event.reminders].sort(
    (a, b) => b.daysBeforeEvent - a.daysBeforeEvent
  );

  return (
    <div className="rounded-lg border bg-card p-6 shadow-sm">
      <h2 className="text-lg font-semibold">리마인더</h2>
      <div className="mt-4 space-y-2">
        {sortedReminders.map((reminder) => (
          <div
            key={reminder.id}
            className="flex items-center rounded-md border border-muted bg-muted/50 px-3 py-2"
          >
            <span className="text-sm">
              {REMINDER_LABELS[reminder.daysBeforeEvent] || `${reminder.daysBeforeEvent}일 전`}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};
