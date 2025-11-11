import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { GiftCard } from "../components/gifts/GiftCard";
import { Button } from "../components/ui/Button";
import { Input } from "../components/ui/Input";
import { useGetGiftsQuery } from "../store/services/giftsApi";

const CATEGORY_FILTERS = [
  { value: "", label: "전체" },
  { value: "ELECTRONICS", label: "전자기기" },
  { value: "FASHION", label: "패션" },
  { value: "FOOD", label: "식품" },
  { value: "BOOK", label: "도서" },
  { value: "HOBBY", label: "취미" },
  { value: "BEAUTY", label: "뷰티" },
  { value: "HOME", label: "생활용품" },
  { value: "OTHER", label: "기타" },
];

const PURCHASE_FILTERS = [
  { value: "all", label: "전체" },
  { value: "purchased", label: "구매 완료" },
  { value: "unpurchased", label: "미구매" },
];

export const GiftListPage = () => {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [category, setCategory] = useState("");
  const [purchaseFilter, setPurchaseFilter] = useState<"all" | "purchased" | "unpurchased">("all");
  const [search, setSearch] = useState("");
  const [searchInput, setSearchInput] = useState("");

  const isPurchased = purchaseFilter === "purchased" ? true : purchaseFilter === "unpurchased" ? false : undefined;

  const { data, isLoading, error } = useGetGiftsQuery({
    page,
    size: 12,
    category: category || undefined,
    isPurchased,
    search: search || undefined,
  });

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setSearch(searchInput);
    setPage(0);
  };

  const handleAddGift = () => {
    navigate("/gifts/new");
  };

  const handleCategoryChange = (value: string) => {
    setCategory(value);
    setPage(0);
  };

  const handlePurchaseFilterChange = (value: "all" | "purchased" | "unpurchased") => {
    setPurchaseFilter(value);
    setPage(0);
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

        <form onSubmit={handleSearchSubmit} className="flex space-x-2">
          <Input
            placeholder="선물명으로 검색..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            className="flex-1"
          />
          <Button type="submit">검색</Button>
        </form>

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

        {data && data.content.length === 0 && (
          <div className="flex h-64 items-center justify-center">
            <div className="text-center">
              <p className="text-muted-foreground">선물이 없습니다.</p>
              <Button onClick={handleAddGift} className="mt-4">
                첫 선물 추가하기
              </Button>
            </div>
          </div>
        )}

        {data && data.content.length > 0 && (
          <>
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
              {data.content.map((gift) => (
                <GiftCard key={gift.id} gift={gift} />
              ))}
            </div>

            {data.totalPages > 1 && (
              <div className="flex items-center justify-center space-x-2">
                <Button
                  onClick={() => setPage(page - 1)}
                  disabled={page === 0}
                  variant="outline"
                >
                  이전
                </Button>
                <span className="text-sm text-muted-foreground">
                  {page + 1} / {data.totalPages}
                </span>
                <Button
                  onClick={() => setPage(page + 1)}
                  disabled={page >= data.totalPages - 1}
                  variant="outline"
                >
                  다음
                </Button>
              </div>
            )}
          </>
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
