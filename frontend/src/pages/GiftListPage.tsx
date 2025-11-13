import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { GiftCard } from "../components/gifts/GiftCard";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { useGetGiftsQuery } from "../store/services/giftsApi";

const CATEGORY_FILTERS = [
  { value: "", label: "전체" },
  { value: "FLOWER", label: "꽃" },
  { value: "JEWELRY", label: "주얼리" },
  { value: "COSMETICS", label: "화장품" },
  { value: "FASHION", label: "패션" },
  { value: "ELECTRONICS", label: "전자기기" },
  { value: "FOOD", label: "음식/디저트" },
  { value: "EXPERIENCE", label: "체험/이벤트" },
  { value: "BOOK", label: "책" },
  { value: "HOBBY", label: "취미용품" },
  { value: "OTHER", label: "기타" },
];

const PURCHASE_FILTERS = [
  { value: "all", label: "전체" },
  { value: "purchased", label: "구매 완료" },
  { value: "unpurchased", label: "미구매" },
];

export const GiftListPage = () => {
  const navigate = useNavigate();
  const [category, setCategory] = useState("");
  const [purchaseFilter, setPurchaseFilter] = useState<"all" | "purchased" | "unpurchased">("all");

  const isPurchased = purchaseFilter === "purchased" ? true : purchaseFilter === "unpurchased" ? false : undefined;

  const { data, isLoading, error } = useGetGiftsQuery({
    category: category || undefined,
    isPurchased,
  });

  const handleAddGift = () => {
    navigate("/gifts/new");
  };

  const handleCategoryChange = (value: string) => {
    setCategory(value);
  };

  const handlePurchaseFilterChange = (value: "all" | "purchased" | "unpurchased") => {
    setPurchaseFilter(value);
  };

  return (
    <PageLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">선물 목록</h1>
          <Button onClick={handleAddGift}>
            선물 추가
          </Button>
        </div>

        <div className="space-y-3">
          <div>
            <label className="mb-2 block text-sm font-medium">카테고리</label>
            <div className="flex flex-wrap gap-2">
              {CATEGORY_FILTERS.map((filter) => (
                <button
                  key={filter.value}
                  onClick={() => handleCategoryChange(filter.value)}
                  className={`rounded-full px-4 py-2 text-sm font-medium transition-colors ${
                    category === filter.value
                      ? "bg-primary text-primary-foreground"
                      : "bg-muted text-muted-foreground hover:bg-muted/80"
                  }`}
                >
                  {filter.label}
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="mb-2 block text-sm font-medium">구매 상태</label>
            <div className="flex flex-wrap gap-2">
              {PURCHASE_FILTERS.map((filter) => (
                <button
                  key={filter.value}
                  onClick={() => handlePurchaseFilterChange(filter.value as "all" | "purchased" | "unpurchased")}
                  className={`rounded-full px-4 py-2 text-sm font-medium transition-colors ${
                    purchaseFilter === filter.value
                      ? "bg-primary text-primary-foreground"
                      : "bg-muted text-muted-foreground hover:bg-muted/80"
                  }`}
                >
                  {filter.label}
                </button>
              ))}
            </div>
          </div>
        </div>

        {isLoading && (
          <div className="flex h-64 items-center justify-center">
            <p className="text-muted-foreground">로딩 중...</p>
          </div>
        )}

        {error && (
          <div className="flex h-64 items-center justify-center">
            <p className="text-red-500">선물 목록을 불러오는데 실패했습니다.</p>
          </div>
        )}

        {data && data.length === 0 && (
          <div className="flex h-64 items-center justify-center">
            <div className="text-center">
              <p className="text-muted-foreground">선물이 없습니다.</p>
              <Button onClick={handleAddGift} className="mt-4">
                첫 선물 추가하기
              </Button>
            </div>
          </div>
        )}

        {data && data.length > 0 && (
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            {data.map((gift) => (
              <GiftCard key={gift.id} gift={gift} />
            ))}
          </div>
        )}
      </div>

      <button
        onClick={handleAddGift}
        className="fixed bottom-6 right-6 flex h-14 w-14 items-center justify-center rounded-full bg-primary text-primary-foreground shadow-lg transition-transform hover:scale-110"
        aria-label="선물 추가"
      >
        <span className="text-2xl">+</span>
      </button>
    </PageLayout>
  );
};
