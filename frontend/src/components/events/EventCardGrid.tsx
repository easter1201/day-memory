import type { Event } from "../../types/event";
import { EventCard } from "./EventCard";

interface EventCardGridProps {
  events: Event[];
}

export const EventCardGrid = ({ events }: EventCardGridProps) => {
  if (!events || events.length === 0) {
    return (
      <div className="flex h-64 items-center justify-center text-muted-foreground">
        <p>등록된 이벤트가 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      {events.map((event) => (
        <EventCard key={event.id} event={event} />
      ))}
    </div>
  );
};
