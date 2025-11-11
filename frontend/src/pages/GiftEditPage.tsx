import { useParams, useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { GiftForm } from "../components/gifts/GiftForm";
import { Button } from "../components/ui/Button";
import { useGetGiftByIdQuery, useUpdateGiftMutation } from "../store/services/giftsApi";
import type { UpdateGiftRequest } from "../types/gift";

export const GiftEditPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: gift, isLoading, error } = useGetGiftByIdQuery(Number(id));
  const [updateGift, { isLoading: isUpdating }] = useUpdateGiftMutation();

  const handleSubmit = async (data: UpdateGiftRequest) => {
    try {
      await updateGift({ id: Number(id), data }).unwrap();
      navigate(`/gifts/${id}`);
    } catch (error) {
      console.error("Failed to update gift:", error);
    }
  };

  const handleCancel = () => {
    navigate(`/gifts/${id}`);
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <p className="text-muted-foreground">로딩 중...</p>
        </div>
      </PageLayout>
    );
  }

  if (error || !gift) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <div className="text-center">
            <p className="text-red-500">선물을 불러오는데 실패했습니다.</p>
            <Button onClick={() => navigate("/gifts")} className="mt-4">
              목록으로 돌아가기
            </Button>
          </div>
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-2xl">
        <h1 className="mb-6 text-2xl font-bold">선물 수정</h1>
        <GiftForm
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          isLoading={isUpdating}
          defaultValues={{
            name: gift.name,
            category: gift.category,
            price: gift.price,
            url: gift.url,
            memo: gift.memo,
            isPurchased: gift.isPurchased,
            eventId: gift.eventId,
          }}
        />
      </div>
    </PageLayout>
  );
};
