import { useParams, useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Checkbox } from "../components/ui/Checkbox";
import { useGetGiftByIdQuery, useTogglePurchasedMutation, useDeleteGiftMutation } from "../store/services/giftsApi";

const CATEGORY_LABELS: Record<string, string> = {
  ELECTRONICS: "전자기기",
  FASHION: "패션",
  FOOD: "식품",
  BOOK: "도서",
  HOBBY: "취미",
  BEAUTY: "뷰티",
  HOME: "생활용품",
  OTHER: "기타",
};

const CATEGORY_COLORS: Record<string, string> = {
  ELECTRONICS: "bg-blue-100 text-blue-800",
  FASHION: "bg-purple-100 text-purple-800",
  FOOD: "bg-orange-100 text-orange-800",
  BOOK: "bg-green-100 text-green-800",
  HOBBY: "bg-pink-100 text-pink-800",
  BEAUTY: "bg-rose-100 text-rose-800",
  HOME: "bg-amber-100 text-amber-800",
  OTHER: "bg-gray-100 text-gray-800",
};

export const GiftDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const { data: gift, isLoading, error } = useGetGiftByIdQuery(Number(id));
  const [togglePurchased] = useTogglePurchasedMutation();
  const [deleteGift, { isLoading: isDeleting }] = useDeleteGiftMutation();

  const handleTogglePurchased = async () => {
    if (!gift) return;
    try {
      await togglePurchased(gift.id).unwrap();
    } catch (error) {
      console.error("Failed to toggle purchased status:", error);
    }
  };

  const handleEdit = () => {
    navigate(`/gifts/${id}/edit`);
  };

  const handleDelete = async () => {
    if (!gift) return;
    if (window.confirm("정말로 이 선물을 삭제하시겠습니까?")) {
      try {
        await deleteGift(gift.id).unwrap();
        navigate("/gifts");
      } catch (error) {
        console.error("Failed to delete gift:", error);
      }
    }
  };

  const handleBack = () => {
    navigate("/gifts");
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("ko-KR", {
      style: "currency",
      currency: "KRW",
    }).format(price);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
    });
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
            <Button onClick={handleBack} className="mt-4">
              목록으로 돌아가기
            </Button>
          </div>
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="mx-auto max-w-3xl space-y-6">
        {/* Gift Header */}
        <div className="space-y-4">
          <Button variant="outline" onClick={handleBack}>
            ← 목록으로
          </Button>

          <div className="rounded-lg border bg-card p-6 shadow-sm">
            <div className="mb-4 flex items-start justify-between">
              <div className="flex-1">
                <h1 className="text-3xl font-bold">{gift.name}</h1>
                <span
                  className={`mt-2 inline-block rounded-full px-3 py-1 text-sm font-medium ${
                    CATEGORY_COLORS[gift.category] || CATEGORY_COLORS.OTHER
                  }`}
                >
                  {CATEGORY_LABELS[gift.category] || gift.category}
                </span>
              </div>
              <div className="flex items-center space-x-2">
                <Button onClick={handleEdit} variant="outline">
                  수정
                </Button>
                <Button onClick={handleDelete} variant="outline" disabled={isDeleting}>
                  {isDeleting ? "삭제 중..." : "삭제"}
                </Button>
              </div>
            </div>

            <div className="mt-6 flex items-center justify-between border-t pt-4">
              <div>
                <p className="text-3xl font-bold text-primary">
                  {formatPrice(gift.price)}
                </p>
              </div>
              <div>
                <Checkbox
                  id={`gift-purchased-${gift.id}`}
                  label="구매 완료"
                  checked={gift.isPurchased}
                  onChange={handleTogglePurchased}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Gift Info Section */}
        <div className="rounded-lg border bg-card p-6 shadow-sm">
          <h2 className="mb-4 text-xl font-semibold">선물 정보</h2>

          <div className="space-y-4">
            {gift.url && (
              <div>
                <label className="mb-1 block text-sm font-medium text-muted-foreground">
                  구매 링크
                </label>
                <a
                  href={gift.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-primary hover:underline"
                >
                  {gift.url}
                </a>
              </div>
            )}

            {gift.memo && (
              <div>
                <label className="mb-1 block text-sm font-medium text-muted-foreground">
                  메모
                </label>
                <p className="whitespace-pre-wrap text-sm">{gift.memo}</p>
              </div>
            )}

            <div className="grid grid-cols-2 gap-4 border-t pt-4">
              <div>
                <label className="mb-1 block text-sm font-medium text-muted-foreground">
                  등록일
                </label>
                <p className="text-sm">{formatDate(gift.createdAt)}</p>
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium text-muted-foreground">
                  수정일
                </label>
                <p className="text-sm">{formatDate(gift.updatedAt)}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Linked Event Section */}
        {gift.eventId && gift.eventTitle && (
          <div className="rounded-lg border bg-card p-6 shadow-sm">
            <h2 className="mb-4 text-xl font-semibold">연결된 이벤트</h2>

            <div
              className="cursor-pointer rounded-lg border bg-muted/50 p-4 transition-colors hover:bg-muted"
              onClick={() => navigate(`/events/${gift.eventId}`)}
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">{gift.eventTitle}</p>
                  <p className="mt-1 text-sm text-muted-foreground">
                    이벤트 상세보기 →
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </PageLayout>
  );
};
