import { useNavigate } from "react-router-dom";
import type { Gift } from "../../types/gift";
import { Checkbox } from "../ui/Checkbox";
import { useTogglePurchasedMutation } from "../../store/services/giftsApi";

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
  const [togglePurchased] = useTogglePurchasedMutation();

  const handleCardClick = () => {
    navigate(`/gifts/${gift.id}`);
  };

  const handleCheckboxChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    e.stopPropagation();
    try {
      await togglePurchased(gift.id).unwrap();
    } catch (error) {
      console.error("Failed to toggle purchased status:", error);
    }
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
      className="cursor-pointer rounded-lg border bg-card p-4 shadow-sm transition-shadow hover:shadow-md"
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <h3 className="text-lg font-semibold">{gift.name}</h3>
          {gift.eventTitle && (
            <p className="mt-1 text-sm text-muted-foreground">
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

      <div className="mt-3 flex items-center justify-between">
        <p className="text-lg font-bold text-primary">
          {formatPrice(gift.price)}
        </p>
        <div onClick={(e) => e.stopPropagation()}>
          <Checkbox
            id={`gift-${gift.id}`}
            label="구매 완료"
            checked={gift.isPurchased}
            onChange={handleCheckboxChange}
          />
        </div>
      </div>
    </div>
  );
};
