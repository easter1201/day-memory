import { useNavigate } from "react-router-dom";
import type { Gift } from "../../types/gift";

interface GiftCardProps {
  gift: Gift;
}

const CATEGORY_LABELS: Record<string, string> = {
  FLOWER: "꽃",
  JEWELRY: "주얼리",
  COSMETICS: "화장품",
  FASHION: "패션",
  ELECTRONICS: "전자기기",
  FOOD: "음식/디저트",
  EXPERIENCE: "체험/이벤트",
  BOOK: "책",
  HOBBY: "취미용품",
  OTHER: "기타",
};

const CATEGORY_COLORS: Record<string, string> = {
  FLOWER: "bg-pink-100 text-pink-800",
  JEWELRY: "bg-purple-100 text-purple-800",
  COSMETICS: "bg-rose-100 text-rose-800",
  FASHION: "bg-indigo-100 text-indigo-800",
  ELECTRONICS: "bg-blue-100 text-blue-800",
  FOOD: "bg-orange-100 text-orange-800",
  EXPERIENCE: "bg-amber-100 text-amber-800",
  BOOK: "bg-green-100 text-green-800",
  HOBBY: "bg-cyan-100 text-cyan-800",
  OTHER: "bg-gray-100 text-gray-800",
};

export const GiftCard = ({ gift }: GiftCardProps) => {
  const navigate = useNavigate();

  const handleCardClick = () => {
    navigate(`/gifts/${gift.id}`);
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat("ko-KR", {
      style: "currency",
      currency: "KRW",
    }).format(price);
  };

  return (
    <div
      onClick={handleCardClick}
      className={`relative cursor-pointer rounded-lg border p-4 shadow-sm transition-all hover:shadow-md ${
        gift.isPurchased
          ? "bg-green-50 border-green-200 opacity-75"
          : "bg-card border-border"
      }`}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h3 className={`text-lg font-semibold ${gift.isPurchased ? "text-green-900" : ""}`}>
            {gift.name}
          </h3>
          {gift.eventTitle && (
            <p className={`mt-1 text-sm ${gift.isPurchased ? "text-green-700" : "text-muted-foreground"}`}>
              연결: {gift.eventTitle}
            </p>
          )}
        </div>
        <span
          className={`rounded-full px-3 py-1 text-xs font-medium ${
            CATEGORY_COLORS[gift.category] || CATEGORY_COLORS.OTHER
          }`}
        >
          {CATEGORY_LABELS[gift.category] || gift.category}
        </span>
      </div>

      <div className="mt-3 space-y-1">
        {gift.budget ? (
          <>
            <div>
              <p className="text-xs text-muted-foreground">예산</p>
              <p className={`text-lg font-bold ${gift.isPurchased ? "text-green-700" : "text-primary"}`}>
                {formatPrice(gift.budget)}
              </p>
            </div>
            {gift.estimatedPrice && (
              <div>
                <p className="text-xs text-muted-foreground">AI 예상 가격</p>
                <p className={`text-sm font-medium ${gift.isPurchased ? "text-green-600" : "text-muted-foreground"}`}>
                  {formatPrice(gift.estimatedPrice)}
                </p>
              </div>
            )}
          </>
        ) : (
          <p className={`text-lg font-bold ${gift.isPurchased ? "text-green-700" : "text-primary"}`}>
            {formatPrice(gift.price)}
          </p>
        )}
      </div>

      {gift.isPurchased && (
        <div className="absolute bottom-3 right-3">
          <span className="flex items-center gap-1 rounded-full bg-green-600 px-3 py-1 text-xs font-medium text-white shadow-sm">
            ✓ 구매완료
          </span>
        </div>
      )}
    </div>
  );
};
