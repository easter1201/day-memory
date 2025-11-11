import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { EventHeader } from "../components/events/EventHeader";
import { EventInfoSection } from "../components/events/EventInfoSection";
import { ReminderSection } from "../components/events/ReminderSection";
import { LinkedGiftsSection } from "../components/events/LinkedGiftsSection";
import { Button } from "../components/ui/Button";
import { useGetEventByIdQuery, useDeleteEventMutation } from "../store/services/eventsApi";

export const EventDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const { data: event, isLoading, error } = useGetEventByIdQuery(Number(id));
  const [deleteEvent, { isLoading: isDeleting }] = useDeleteEventMutation();

  const handleEdit = () => {
    navigate(`/events/${id}/edit`);
  };

  const handleDelete = async () => {
    try {
      await deleteEvent(Number(id)).unwrap();
      navigate("/events");
    } catch (error) {
      console.error("Failed to delete event:", error);
    }
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
      <div className="mx-auto max-w-4xl space-y-6">
        <EventHeader event={event} />

        <div className="flex space-x-3">
          <Button onClick={handleEdit} className="flex-1">
            수정
          </Button>
          <Button
            onClick={() => setShowDeleteConfirm(true)}
            variant="destructive"
            className="flex-1"
          >
            삭제
          </Button>
        </div>

        <EventInfoSection event={event} />

        <ReminderSection event={event} />

        <LinkedGiftsSection event={event} />

        {/* Delete Confirmation Dialog */}
        {showDeleteConfirm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
            <div className="w-full max-w-md rounded-lg bg-card p-6 shadow-lg">
              <h2 className="text-lg font-semibold">이벤트 삭제</h2>
              <p className="mt-2 text-sm text-muted-foreground">
                이 이벤트를 정말 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.
              </p>
              <div className="mt-6 flex space-x-3">
                <Button
                  onClick={() => setShowDeleteConfirm(false)}
                  variant="outline"
                  className="flex-1"
                  disabled={isDeleting}
                >
                  취소
                </Button>
                <Button
                  onClick={handleDelete}
                  variant="destructive"
                  className="flex-1"
                  isLoading={isDeleting}
                >
                  삭제
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </PageLayout>
  );
};
