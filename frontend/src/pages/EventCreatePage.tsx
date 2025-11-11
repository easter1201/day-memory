import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { EventForm } from "../components/events/EventForm";
import { useCreateEventMutation } from "../store/services/eventsApi";
import type { CreateEventRequest } from "../types/event";

export const EventCreatePage = () => {
  const navigate = useNavigate();
  const [createEvent, { isLoading }] = useCreateEventMutation();

  const handleSubmit = async (data: CreateEventRequest) => {
    try {
      const result = await createEvent(data).unwrap();
      navigate(`/events/${result.id}`);
    } catch (error) {
      console.error("Failed to create event:", error);
    }
  };

  const handleCancel = () => {
    navigate("/events");
  };

  return (
    <PageLayout>
      <div className="mx-auto max-w-2xl space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">새 이벤트 만들기</h1>
        </div>

        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <EventForm
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            isLoading={isLoading}
          />
        </div>
      </div>
    </PageLayout>
  );
};
