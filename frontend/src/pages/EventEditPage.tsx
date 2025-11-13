import { useParams, useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { EventForm } from "../components/events/EventForm";
import { useGetEventByIdQuery, useUpdateEventMutation } from "../store/services/eventsApi";
import type { UpdateEventRequest } from "../types/event";

export const EventEditPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: event, isLoading, error } = useGetEventByIdQuery(Number(id));
  const [updateEvent, { isLoading: isUpdating }] = useUpdateEventMutation();

  const handleSubmit = async (data: UpdateEventRequest) => {
    try {
      await updateEvent({ id: Number(id), data }).unwrap();
      navigate(`/events/${id}`);
    } catch (error) {
      console.error("Failed to update event:", error);
    }
  };

  const handleCancel = () => {
    navigate(`/events/${id}`);
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-full items-center justify-center">
          <p className="text-muted-foreground">로딩 중...</p>
        </div>
      </PageLayout>
    );
  }

  if (error || !event) {
    return (
      <PageLayout>
        <div className="flex h-full items-center justify-center">
          <p className="text-red-500">이벤트를 불러오는데 실패했습니다.</p>
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-2xl">
        <h1 className="mb-6 text-2xl font-bold">이벤트 수정</h1>

        <EventForm
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          isLoading={isUpdating}
          defaultValues={{
            title: event.title,
            description: event.description,
            recipientName: event.recipientName,
            relationship: event.relationship,
            eventDate: event.eventDate,
            eventType: event.eventType,
            isRecurring: event.isRecurring,
            isTracking: event.isTracking,
            reminderDays: event.reminders?.map((r) => r.daysBeforeEvent) || [],
          }}
        />
      </div>
    </PageLayout>
  );
};
