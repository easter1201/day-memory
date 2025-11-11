import { useNavigate } from "react-router-dom";
import type { Event } from "../../types/dashboard";

interface UpcomingEventsWidgetProps {
  events: Event[];
}

export const UpcomingEventsWidget = ({ events }: UpcomingEventsWidgetProps) => {
  const navigate = useNavigate();

  const calculateDDay = (eventDate: string) => {
    const today = new Date();
    const target = new Date(eventDate);
    const diffTime = target.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return "D-Day";
    if (diffDays > 0) return `D-${diffDays}`;
    return `D+${Math.abs(diffDays)}`;
  };

  return (
    <div className="rounded-lg border bg-card p-6 shadow-sm">
      <h2 className="text-lg font-semibold">다가오는 이벤트</h2>
      <div className="mt-4 space-y-3">
        {events.length === 0 ? (
          <p className="text-sm text-muted-foreground">다가오는 이벤트가 없습니다.</p>
        ) : (
          events.map((event) => (
            <div
              key={event.id}
              onClick={() => navigate(`/events/${event.id}`)}
              className="flex cursor-pointer items-center justify-between rounded-lg border p-3 transition-colors hover:bg-accent"
            >
              <div>
                <p className="font-medium">{event.title}</p>
                <p className="text-sm text-muted-foreground">
                  {event.recipientName} · {new Date(event.eventDate).toLocaleDateString()}
                </p>
              </div>
              <div className="flex-shrink-0">
                <span className="rounded-full bg-primary px-3 py-1 text-sm font-medium text-primary-foreground">
                  {calculateDDay(event.eventDate)}
                </span>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
