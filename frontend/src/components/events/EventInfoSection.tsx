import type { Event } from "../../types/event";
import { formatDate, getRelationshipLabel } from "../../utils/dateUtils";

interface EventInfoSectionProps {
  event: Event;
}

export const EventInfoSection = ({ event }: EventInfoSectionProps) => {
  return (
    <div className="space-y-4 rounded-lg border bg-card p-6 shadow-sm">
      <h2 className="text-lg font-semibold">이벤트 정보</h2>

      <div className="space-y-3">
        <div className="flex items-start">
          <span className="w-24 text-sm font-medium text-muted-foreground">날짜</span>
          <span className="flex-1 text-sm">{formatDate(event.eventDate)}</span>
        </div>

        <div className="flex items-start">
          <span className="w-24 text-sm font-medium text-muted-foreground">대상자</span>
          <span className="flex-1 text-sm">{event.recipientName}</span>
        </div>

        <div className="flex items-start">
          <span className="w-24 text-sm font-medium text-muted-foreground">관계</span>
          <span className="flex-1 text-sm">{getRelationshipLabel(event.relationship)}</span>
        </div>

        {event.memo && (
          <div className="flex items-start">
            <span className="w-24 text-sm font-medium text-muted-foreground">메모</span>
            <span className="flex-1 whitespace-pre-wrap text-sm">{event.memo}</span>
          </div>
        )}

        <div className="flex items-start">
          <span className="w-24 text-sm font-medium text-muted-foreground">추적 여부</span>
          <span className="flex-1 text-sm">{event.isTracked ? "추적 중" : "추적 안 함"}</span>
        </div>
      </div>
    </div>
  );
};
