import type { Event } from "../../types/event";

interface LinkedGiftsSectionProps {
  event: Event;
}

export const LinkedGiftsSection = ({ event }: LinkedGiftsSectionProps) => {
  // TODO: Implement linked gifts functionality when gift items feature is ready
  // For now, show placeholder

  return (
    <div className="rounded-lg border bg-card p-6 shadow-sm">
      <h2 className="text-lg font-semibold">연결된 선물</h2>
      <p className="mt-4 text-sm text-muted-foreground">
        아직 연결된 선물이 없습니다.
      </p>
    </div>
  );
};
