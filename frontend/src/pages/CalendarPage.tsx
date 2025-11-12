import { useState, useMemo, useCallback } from "react";
import { Calendar, dateFnsLocalizer, View } from "react-big-calendar";
import { format, parse, startOfWeek, getDay, addMonths, subMonths } from "date-fns";
import { ko } from "date-fns/locale";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { EventPopup } from "../components/calendar/EventPopup";
import { useGetEventsByMonthQuery } from "../store/services/eventsApi";
import { Event } from "../types/event";
import "react-big-calendar/lib/css/react-big-calendar.css";

// date-fns localizer 설정
const locales = {
  ko: ko,
};

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek: () => startOfWeek(new Date(), { locale: ko }),
  getDay,
  locales,
});

// 한국어 메시지
const messages = {
  allDay: "종일",
  previous: "이전",
  next: "다음",
  today: "오늘",
  month: "월",
  week: "주",
  day: "일",
  agenda: "일정",
  date: "날짜",
  time: "시간",
  event: "이벤트",
  noEventsInRange: "이 범위에는 이벤트가 없습니다.",
  showMore: (total: number) => `+${total} 더보기`,
};

interface CalendarEvent {
  id: number;
  title: string;
  start: Date;
  end: Date;
  resource: Event;
}

export const CalendarPage = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);
  const [isPopupOpen, setIsPopupOpen] = useState(false);
  const [view, setView] = useState<View>("month");

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth() + 1;

  const { data: events, isLoading } = useGetEventsByMonthQuery({ year, month });

  // Event 데이터를 react-big-calendar 형식으로 변환
  const calendarEvents: CalendarEvent[] = useMemo(() => {
    if (!events) return [];

    return events.map((event) => {
      const eventDate = new Date(event.eventDate);
      return {
        id: event.id,
        title: `${event.title} - ${event.recipientName}`,
        start: eventDate,
        end: eventDate,
        resource: event,
      };
    });
  }, [events]);

  const handleSelectEvent = useCallback((event: CalendarEvent) => {
    setSelectedEvent(event.resource);
    setIsPopupOpen(true);
  }, []);

  const handleNavigate = useCallback((newDate: Date) => {
    setCurrentDate(newDate);
  }, []);

  const handleViewChange = useCallback((newView: View) => {
    setView(newView);
  }, []);

  const handlePrevMonth = () => {
    setCurrentDate(subMonths(currentDate, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(addMonths(currentDate, 1));
  };

  const handleToday = () => {
    setCurrentDate(new Date());
  };

  // 이벤트 스타일 커스터마이징
  const eventStyleGetter = (event: CalendarEvent) => {
    const eventType = event.resource.eventType;
    let backgroundColor = "#3174ad";

    switch (eventType) {
      case "BIRTHDAY":
        backgroundColor = "#ec4899"; // pink
        break;
      case "ANNIVERSARY":
        backgroundColor = "#8b5cf6"; // purple
        break;
      case "HOLIDAY":
        backgroundColor = "#f59e0b"; // amber
        break;
      default:
        backgroundColor = "#3174ad"; // blue
    }

    return {
      style: {
        backgroundColor,
        borderRadius: "4px",
        opacity: 0.9,
        color: "white",
        border: "0px",
        display: "block",
        fontSize: "0.875rem",
        padding: "2px 5px",
      },
    };
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <LoadingSpinner size="lg" text="캘린더를 불러오는 중..." />
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-7xl space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold">캘린더</h1>
            <p className="mt-1 text-muted-foreground">
              월별 이벤트를 한눈에 확인하세요
            </p>
          </div>

          {/* Navigation Controls */}
          <div className="flex items-center gap-2">
            <Button variant="outline" size="sm" onClick={handlePrevMonth}>
              이전 달
            </Button>
            <Button variant="outline" size="sm" onClick={handleToday}>
              오늘
            </Button>
            <Button variant="outline" size="sm" onClick={handleNextMonth}>
              다음 달
            </Button>
          </div>
        </div>

        {/* Current Month Display */}
        <div className="text-center">
          <h2 className="text-2xl font-semibold">
            {format(currentDate, "yyyy년 M월", { locale: ko })}
          </h2>
        </div>

        {/* Calendar */}
        <div className="rounded-lg border bg-card p-4 shadow-sm">
          <div style={{ height: "700px" }}>
            <Calendar
              localizer={localizer}
              events={calendarEvents}
              startAccessor="start"
              endAccessor="end"
              style={{ height: "100%" }}
              onSelectEvent={handleSelectEvent}
              onNavigate={handleNavigate}
              onView={handleViewChange}
              view={view}
              date={currentDate}
              messages={messages}
              eventPropGetter={eventStyleGetter}
              culture="ko"
              formats={{
                monthHeaderFormat: "yyyy년 M월",
                dayHeaderFormat: "M월 d일 (eee)",
                dayRangeHeaderFormat: ({ start, end }) =>
                  `${format(start, "M월 d일", { locale: ko })} - ${format(end, "M월 d일", { locale: ko })}`,
                agendaHeaderFormat: ({ start, end }) =>
                  `${format(start, "M월 d일", { locale: ko })} - ${format(end, "M월 d일", { locale: ko })}`,
              }}
            />
          </div>
        </div>

        {/* Event Legend */}
        <div className="rounded-lg border bg-card p-4 shadow-sm">
          <h3 className="mb-2 font-semibold">이벤트 타입</h3>
          <div className="flex flex-wrap gap-4">
            <div className="flex items-center gap-2">
              <div className="h-4 w-4 rounded" style={{ backgroundColor: "#ec4899" }}></div>
              <span className="text-sm">생일</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="h-4 w-4 rounded" style={{ backgroundColor: "#8b5cf6" }}></div>
              <span className="text-sm">기념일</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="h-4 w-4 rounded" style={{ backgroundColor: "#f59e0b" }}></div>
              <span className="text-sm">명절</span>
            </div>
          </div>
        </div>
      </div>

      {/* Event Popup */}
      <EventPopup
        event={selectedEvent}
        isOpen={isPopupOpen}
        onClose={() => {
          setIsPopupOpen(false);
          setSelectedEvent(null);
        }}
      />
    </PageLayout>
  );
};
