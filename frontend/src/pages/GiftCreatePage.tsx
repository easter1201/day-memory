import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { GiftForm } from "../components/gifts/GiftForm";
import { useCreateGiftMutation } from "../store/services/giftsApi";
import type { CreateGiftRequest } from "../types/gift";

export const GiftCreatePage = () => {
  const navigate = useNavigate();
  const [createGift, { isLoading }] = useCreateGiftMutation();

  const handleSubmit = async (data: CreateGiftRequest) => {
    try {
      await createGift(data).unwrap();
      navigate("/gifts");
    } catch (error) {
      console.error("Failed to create gift:", error);
    }
  };

  const handleCancel = () => {
    navigate("/gifts");
  };

  return (
    <PageLayout>
      <div className="mx-auto max-w-2xl">
        <h1 className="mb-6 text-2xl font-bold">선물 추가</h1>
        <GiftForm
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          isLoading={isLoading}
        />
      </div>
    </PageLayout>
  );
};
