import { Event } from "../../types/event";
import { Modal } from "../common/Modal";
import { Badge } from "../ui/Badge";
import { Button } from "../ui/Button";
import { formatDate, calculateDDay } from "../../utils/dateUtils";
import { useNavigate } from "react-router-dom";

interface EventPopupProps {
  event: Event | null;
  isOpen: boolean;
  onClose: () => void;
}

const EVENT_TYPE_LABELS: Record<string, string> = {
  BIRTHDAY: "생일",
  ANNIVERSARY: "기념일",
  HOLIDAY: "명절",
};

export const EventPopup: React.FC<EventPopupProps> = ({ event, isOpen, onClose }) => {
  const navigate = useNavigate();

  if (!event) return null;

  const handleViewDetails = () => {
    navigate(`/events/${event.id}`);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} size="md">
      <div className="space-y-4">
        {/* Header */}
        <div className="border-b pb-3">
          <div className="flex items-center gap-2">
            <h2 className="text-xl font-bold">{event.title}</h2>
            <Badge variant={event.eventType.toLowerCase() as any}>
              {EVENT_TYPE_LABELS[event.eventType] || event.eventType}
            </Badge>
          </div>
        </div>

        {/* Event Info */}
        <div className="space-y-3">
          <div>
            <label className="text-sm font-medium text-muted-foreground">날짜</label>
            <p className="text-base">
              {formatDate(event.eventDate)} ({calculateDDay(event.eventDate)})
            </p>
          </div>

          <div>
            <label className="text-sm font-medium text-muted-foreground">대상</label>
            <p className="text-base">
              {event.recipientName}
              {event.relationship && ` (${event.relationship})`}
            </p>
          </div>

          {event.memo && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">메모</label>
              <p className="text-base text-muted-foreground">{event.memo}</p>
            </div>
          )}

          <div>
            <label className="text-sm font-medium text-muted-foreground">추적 상태</label>
            <p className="text-base">
              {event.isTracked ? (
                <span className="text-green-600">추적 중</span>
              ) : (
                <span className="text-gray-600">미추적</span>
              )}
            </p>
          </div>

          {event.reminders && event.reminders.length > 0 && (
            <div>
              <label className="text-sm font-medium text-muted-foreground">리마인더</label>
              <div className="mt-1 flex flex-wrap gap-2">
                {event.reminders.map((days) => (
                  <Badge key={days} variant="outline">
                    {days}일 전
                  </Badge>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="flex justify-end gap-2 border-t pt-3">
          <Button variant="outline" onClick={onClose}>
            닫기
          </Button>
          <Button onClick={handleViewDetails}>상세 보기</Button>
        </div>
      </div>
    </Modal>
  );
};
