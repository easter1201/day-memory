import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Badge } from "../components/ui/Badge";
import { LoadingSpinner } from "../components/common/LoadingSpinner";
import { EmptyState } from "../components/common/EmptyState";
import { Pagination } from "../components/common/Pagination";
import { useGetRecommendationsQuery } from "../store/services/recommendationsApi";

const STATUS_LABELS = {
  PENDING: "처리 중",
  COMPLETED: "완료",
  FAILED: "실패",
};

const STATUS_COLORS = {
  PENDING: "bg-yellow-100 text-yellow-800",
  COMPLETED: "bg-green-100 text-green-800",
  FAILED: "bg-red-100 text-red-800",
};

export const RecommendationsListPage = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);

  const { data, isLoading } = useGetRecommendationsQuery({
    page,
    size: 12,
  });

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
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
          <LoadingSpinner size="lg" text="추천 내역을 불러오는 중..." />
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-6xl">
        {/* Header */}
        <div className="mb-6 flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold">AI 선물 추천 내역</h1>
            <p className="mt-1 text-muted-foreground">
              AI가 추천한 선물 내역을 확인하세요
            </p>
          </div>
          <Button onClick={() => navigate("/recommendations/new")}>
            새 추천 요청
          </Button>
        </div>

        {/* Recommendations List */}
        {!data || data.content.length === 0 ? (
          <EmptyState
            title="아직 추천 내역이 없습니다"
            description="AI에게 선물 추천을 요청해보세요"
            action={{
              label: "추천 요청하기",
              onClick: () => navigate("/recommendations/new"),
            }}
          />
        ) : (
          <>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
              {data.content.map((recommendation) => (
                <div
                  key={recommendation.id}
                  onClick={() => navigate(`/recommendations/${recommendation.id}`)}
                  className="cursor-pointer rounded-lg border bg-card p-4 shadow-sm transition-shadow hover:shadow-md"
                >
                  <div className="mb-3 flex items-start justify-between">
                    <div className="flex-1">
                      <h3 className="font-semibold">{recommendation.eventTitle}</h3>
                      <p className="text-sm text-muted-foreground">
                        {recommendation.recipientName}
                      </p>
                    </div>
                    <Badge
                      className={`text-xs ${
                        STATUS_COLORS[recommendation.status]
                      }`}
                    >
                      {STATUS_LABELS[recommendation.status]}
                    </Badge>
                  </div>

                  <div className="mb-3 space-y-1 text-sm">
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">예산</span>
                      <span className="font-medium">
                        {formatPrice(recommendation.budget)}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">AI 추천</span>
                      <span className="font-medium">
                        {recommendation.aiRecommendations.length}개
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-muted-foreground">내 선물</span>
                      <span className="font-medium">
                        {recommendation.userSavedGifts.length}개
                      </span>
                    </div>
                  </div>

                  <div className="border-t pt-2 text-xs text-muted-foreground">
                    {formatDate(recommendation.createdAt)}
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            {data.totalPages > 1 && (
              <div className="mt-6">
                <Pagination
                  currentPage={page + 1}
                  totalPages={data.totalPages}
                  onPageChange={(newPage) => setPage(newPage - 1)}
                />
              </div>
            )}
          </>
        )}
      </div>
    </PageLayout>
  );
};
