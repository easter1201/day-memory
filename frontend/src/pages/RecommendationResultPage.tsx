import { useParams, useNavigate, useLocation } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { ErrorMessage } from "../components/common/ErrorMessage";
import {
  useGetRecommendationByIdQuery,
} from "../store/services/recommendationsApi";
import { useCreateGiftMutation } from "../store/services/giftsApi";
import { GIFT_CATEGORY_COLORS } from "../constants";
import Toast from "../components/common/Toast";
import { useState } from "react";

export const RecommendationResultPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();

  // state로 전달된 추천 결과 확인
  const stateData = location.state as { recommendation?: any; request?: any } | null;

  const { data: recommendation, isLoading, error, refetch } = useGetRecommendationByIdQuery(Number(id));
  const [createGift, { isLoading: isCreatingGift }] = useCreateGiftMutation();
  const [savedGiftIds, setSavedGiftIds] = useState<Set<number>>(new Set());

  // API 데이터를 우선 사용 (최신 상태 반영)
  const displayData = recommendation || stateData?.recommendation;

  const handleSaveToGiftList = async (recommendation: any, index: number) => {
    try {
      // Get the event ID from state or API data
      const eventId = stateData?.request?.eventId;

      // Get budget from API response (displayData.budget) or state data
      // displayData.budget comes from the saved recommendation in the database
      const budget = displayData?.budget || stateData?.request?.budget;

      await createGift({
        name: recommendation.name,
        category: recommendation.category,
        price: recommendation.estimatedPrice, // Keep estimatedPrice as price for backward compatibility
        estimatedPrice: recommendation.estimatedPrice,
        budget: budget || undefined, // Use budget from API or state
        url: recommendation.purchaseLink || undefined,
        description: recommendation.reason || undefined,
        eventId: eventId || undefined,
      }).unwrap();

      setSavedGiftIds(prev => new Set(prev).add(index));

      // 데이터를 다시 불러와서 isUserSaved 상태를 업데이트
      if (id && !isNaN(Number(id))) {
        await refetch();
      }

      Toast.success("선물이 선물 탭에 저장되었습니다");
    } catch (error) {
      console.error("Failed to save gift to gift list:", error);
      Toast.error("선물 저장에 실패했습니다");
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("ko-KR", {
      style: "currency",
      currency: "KRW",
    }).format(price);
  };

  if (isLoading && !stateData) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <LoadingSpinner size="lg" text="추천 결과를 불러오는 중..." />
        </div>
      </PageLayout>
    );
  }

  if (!displayData) {
    return (
      <PageLayout>
        <div className="flex h-64 items-center justify-center">
          <ErrorMessage
            title="추천 결과를 불러올 수 없습니다"
            message="잠시 후 다시 시도해주세요"
            onRetry={() => navigate("/recommendations")}
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
              <span className="font-medium">{displayData.eventTitle}</span>
            </div>
            {displayData.daysUntilEvent !== undefined && (
              <div className="flex items-center justify-between">
                <span className="text-sm text-muted-foreground">D-Day</span>
                <span className="font-medium">{displayData.daysUntilEvent}일 남음</span>
              </div>
            )}
            {stateData?.request && (
              <>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">예산</span>
                  <span className="font-medium">{formatPrice(stateData.request.budget)}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">선호 카테고리</span>
                  <div className="flex flex-wrap gap-1">
                    {stateData.request.preferredCategories.map((cat: string) => (
                      <Badge key={cat} className="text-xs">
                        {cat}
                      </Badge>
                    ))}
                  </div>
                </div>
                {stateData.request.recipientGender && (
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-muted-foreground">성별</span>
                    <span className="font-medium">{stateData.request.recipientGender === "MALE" ? "남성" : "여성"}</span>
                  </div>
                )}
                {stateData.request.recipientAge && (
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-muted-foreground">나이</span>
                    <span className="font-medium">{stateData.request.recipientAge}세</span>
                  </div>
                )}
                {stateData.request.additionalMessage && (
                  <div className="border-t pt-2">
                    <span className="text-sm text-muted-foreground">제외할 선물</span>
                    <p className="mt-1 text-sm">{stateData.request.additionalMessage}</p>
                  </div>
                )}
              </>
            )}
          </div>
        </div>

        {/* AI Recommendations Section */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">
            AI 추천 선물 ({displayData.recommendations?.length || 0}개)
          </h2>

          {/* Warning Notice */}
          <div className="mb-6 rounded-md bg-amber-50 border border-amber-200 p-4">
            <div className="flex items-start">
              <svg
                className="h-5 w-5 text-amber-500 mt-0.5 mr-3 flex-shrink-0"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z"
                  clipRule="evenodd"
                />
              </svg>
              <div className="flex-1">
                <h3 className="text-sm font-medium text-amber-800">주의사항</h3>
                <p className="mt-1 text-sm text-amber-700">
                  AI가 추천한 제품명과 가격은 예상값으로, 실제와 다를 수 있습니다.
                  구매 전 반드시 정확한 정보를 확인해주세요.
                </p>
              </div>
            </div>
          </div>

          {displayData.recommendations && displayData.recommendations.length > 0 ? (
            <div className="grid gap-6 md:grid-cols-2">
              {displayData.recommendations.map((gift: any, index: number) => (
                <div key={gift.id || index} className="rounded-lg border p-4">
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
                    {formatPrice(gift.estimatedPrice)}
                  </p>

                  <div className="mb-3 rounded-md bg-muted p-3">
                    <p className="text-sm font-medium text-muted-foreground">추천 이유</p>
                    <p className="mt-1 text-sm">{gift.reason}</p>
                  </div>

                  {gift.purchaseLink && (
                    <a
                      href={gift.purchaseLink}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="mb-2 block text-sm text-primary hover:underline"
                    >
                      구매 링크 →
                    </a>
                  )}

                  {gift.isUserSaved || savedGiftIds.has(index) ? (
                    <Button
                      variant="outline"
                      size="sm"
                      disabled
                      className="mt-2 w-full"
                    >
                      ✓ 저장됨
                    </Button>
                  ) : (
                    <Button
                      variant="default"
                      size="sm"
                      onClick={() => handleSaveToGiftList(gift, index)}
                      disabled={isCreatingGift}
                      className="mt-2 w-full"
                    >
                      선물 탭에 저장하기
                    </Button>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <ErrorMessage
              title="추천 결과 없음"
              message="추천된 선물이 없습니다"
            />
          )}
        </div>
      </div>
    </PageLayout>
  );
};
