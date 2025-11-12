import { useParams, useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { ErrorMessage } from "../components/common/ErrorMessage";
import {
  useGetRecommendationByIdQuery,
  useSaveRecommendedGiftMutation,
} from "../store/services/recommendationsApi";
import { GIFT_CATEGORY_COLORS } from "../constants";
import Toast from "../components/common/Toast";

export const RecommendationResultPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: recommendation, isLoading, error } = useGetRecommendationByIdQuery(Number(id));
  const [saveGift, { isLoading: isSaving }] = useSaveRecommendedGiftMutation();

  const handleSaveGift = async (giftId: number) => {
    try {
      await saveGift({
        recommendationId: Number(id),
        recommendedGiftId: giftId,
      }).unwrap();
      Toast.success("선물이 내 리스트에 저장되었습니다");
    } catch (error) {
      console.error("Failed to save gift:", error);
      Toast.error("선물 저장에 실패했습니다");
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("ko-KR", {
      style: "currency",
      currency: "KRW",
    }).format(price);
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <LoadingSpinner size="lg" text="추천 결과를 불러오는 중..." />
        </div>
      </PageLayout>
    );
  }

  if (error || !recommendation) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <ErrorMessage
            title="추천 결과를 불러올 수 없습니다"
            message="잠시 후 다시 시도해주세요"
            onRetry={() => window.location.reload()}
          />
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-5xl space-y-6">
        {/* Header */}
        <div>
          <Button variant="outline" onClick={() => navigate("/recommendations")}>
            ← 추천 내역으로
          </Button>
          <h1 className="mt-4 text-3xl font-bold">AI 선물 추천 결과</h1>
        </div>

        {/* Request Info Section */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">요청 정보</h2>
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">이벤트</span>
              <span className="font-medium">{recommendation.eventTitle}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">받는 사람</span>
              <span className="font-medium">{recommendation.recipientName}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">예산</span>
              <span className="font-medium">{formatPrice(recommendation.budget)}</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">선호 카테고리</span>
              <div className="flex flex-wrap gap-1">
                {recommendation.preferredCategories.map((cat) => (
                  <Badge key={cat} className="text-xs">
                    {cat}
                  </Badge>
                ))}
              </div>
            </div>
            {recommendation.additionalMessage && (
              <div className="border-t pt-2">
                <span className="text-sm text-muted-foreground">추가 메시지</span>
                <p className="mt-1 text-sm">{recommendation.additionalMessage}</p>
              </div>
            )}
          </div>
        </div>

        {/* User Saved Gifts Section */}
        {recommendation.userSavedGifts.length > 0 && (
          <div className="rounded-lg border bg-card p-6 shadow-sm">
            <h2 className="mb-4 text-xl font-semibold">
              내가 저장한 선물 ({recommendation.userSavedGifts.length}개)
            </h2>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {recommendation.userSavedGifts.map((gift) => (
                <div key={gift.id} className="rounded-lg border p-4">
                  <div className="mb-2 flex items-start justify-between">
                    <h3 className="font-medium">{gift.name}</h3>
                    {gift.isPurchased && (
                      <Badge variant="default" className="text-xs">
                        구매완료
                      </Badge>
                    )}
                  </div>
                  <p className="text-sm text-muted-foreground">{gift.category}</p>
                  <p className="mt-2 font-semibold text-primary">{formatPrice(gift.price)}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* AI Recommendations Section */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">
            AI 추천 선물 ({recommendation.aiRecommendations.length}개)
          </h2>
          {recommendation.status === "PENDING" ? (
            <div className="flex items-center justify-center py-8">
              <LoadingSpinner text="AI가 선물을 분석하고 있습니다..." />
            </div>
          ) : recommendation.status === "FAILED" ? (
            <ErrorMessage
              title="추천 생성 실패"
              message="AI 추천을 생성하는 중 오류가 발생했습니다"
            />
          ) : (
            <div className="grid gap-6 md:grid-cols-2">
              {recommendation.aiRecommendations.map((gift) => (
                <div key={gift.id} className="rounded-lg border p-4">
                  <div className="mb-3 flex items-start justify-between">
                    <div>
                      <h3 className="text-lg font-semibold">{gift.name}</h3>
                      <Badge
                        className={`mt-1 text-xs ${
                          GIFT_CATEGORY_COLORS[gift.category as keyof typeof GIFT_CATEGORY_COLORS] ||
                          "bg-gray-100 text-gray-800"
                        }`}
                      >
                        {gift.category}
                      </Badge>
                    </div>
                  </div>

                  <p className="mb-3 text-lg font-bold text-primary">
                    약 {formatPrice(gift.estimatedPrice)}
                  </p>

                  <div className="mb-3 rounded-md bg-muted p-3">
                    <p className="text-sm font-medium text-muted-foreground">추천 이유</p>
                    <p className="mt-1 text-sm">{gift.reason}</p>
                  </div>

                  {gift.url && (
                    <a
                      href={gift.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="mb-2 block text-sm text-primary hover:underline"
                    >
                      구매 링크 →
                    </a>
                  )}

                  <Button
                    onClick={() => handleSaveGift(gift.id)}
                    disabled={isSaving}
                    className="w-full"
                    size="sm"
                  >
                    내 리스트에 저장
                  </Button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </PageLayout>
  );
};
