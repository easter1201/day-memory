import { useNavigate } from "react-router-dom";
import type { Event } from "../../types/event";

interface EventCardProps {
  event: Event;
}

export const EventCard = ({ event }: EventCardProps) => {
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

  const getEventTypeBadgeColor = (eventType: string) => {
    const colors: Record<string, string> = {
      BIRTHDAY: "bg-blue-100 text-blue-800",
      ANNIVERSARY: "bg-pink-100 text-pink-800",
      HOLIDAY: "bg-green-100 text-green-800",
      OTHER: "bg-gray-100 text-gray-800",
    };
    return colors[eventType] || colors.OTHER;
  };

  return (
    <div
      onClick={() => navigate(`/events/${event.id}`)}
      className="cursor-pointer rounded-lg border bg-card p-4 shadow-sm transition-shadow hover:shadow-md"
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h3 className="font-semibold text-lg">{event.title}</h3>
          <p className="mt-1 text-sm text-muted-foreground">{event.recipientName}</p>
          <p className="mt-1 text-sm text-muted-foreground">
            {new Date(event.eventDate).toLocaleDateString()}
          </p>
        </div>
        <div className="ml-4 flex flex-col items-end space-y-2">
          <span className="rounded-full bg-primary px-3 py-1 text-sm font-medium text-primary-foreground">
            {calculateDDay(event.eventDate)}
          </span>
          <span className={`rounded-full px-2 py-1 text-xs ${getEventTypeBadgeColor(event.eventType)}`}>
            {event.eventType}
          </span>
        </div>
      </div>
    </div>
  );
};
