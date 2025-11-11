import { useState, useEffect } from "react";
import type { Event } from "../../types/event";
import { calculateDDay, getEventTypeBadgeColor, getEventTypeLabel } from "../../utils/dateUtils";

interface EventHeaderProps {
  event: Event;
}

export const EventHeader = ({ event }: EventHeaderProps) => {
  const [dDay, setDDay] = useState(calculateDDay(event.eventDate));

  useEffect(() => {
    // Update D-Day every minute
    const interval = setInterval(() => {
      setDDay(calculateDDay(event.eventDate));
    }, 60000);

    return () => clearInterval(interval);
  }, [event.eventDate]);

  return (
    <div className="space-y-4">
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h1 className="text-3xl font-bold">{event.title}</h1>
          <p className="mt-2 text-lg text-muted-foreground">{event.recipientName}</p>
        </div>
        <div className="ml-4 flex flex-col items-end space-y-2">
          <span className="rounded-full bg-primary px-4 py-2 text-xl font-bold text-primary-foreground">
            {dDay}
          </span>
          <span className={`rounded-full px-3 py-1 text-sm font-medium ${getEventTypeBadgeColor(event.eventType)}`}>
            {getEventTypeLabel(event.eventType)}
          </span>
        </div>
      </div>
    </div>
  );
};
