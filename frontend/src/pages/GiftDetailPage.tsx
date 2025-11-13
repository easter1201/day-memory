import { useParams, useNavigate } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/Button";
import { Checkbox } from "../components/ui/Checkbox";
import { useGetGiftByIdQuery, useTogglePurchasedMutation, useDeleteGiftMutation } from "../store/services/giftsApi";
import { useSearchProductsQuery } from "../store/services/shoppingApi";

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

            {gift.description && (
              <div>
                <label className="mb-1 block text-sm font-medium text-muted-foreground">
                  설명
                </label>
                <p className="whitespace-pre-wrap text-sm">{gift.description}</p>
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

        {/* Product Recommendations Section */}
        <ProductRecommendations
          giftName={gift.name}
          giftPrice={gift.estimatedPrice || gift.price}
        />
      </div>
    </PageLayout>
  );
};

// Product Recommendations Component
const ProductRecommendations = ({ giftName, giftPrice }: { giftName: string; giftPrice: number }) => {
  const { data: products, isLoading, error } = useSearchProductsQuery({
    query: giftName,
    display: 20, // 더 많은 결과를 가져와서 필터링
  });

  if (isLoading) {
    return (
      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <h2 className="mb-4 text-xl font-semibold">추천 상품</h2>
        <p className="text-center text-muted-foreground">상품을 검색하는 중...</p>
      </div>
    );
  }

  // 에러가 있으면 표시하지 않음
  if (error) {
    console.error(`[Shopping API Error] 선물: "${giftName}", 예산: ${giftPrice}`, error);
    return null;
  }

  // 검색 결과가 없으면 메시지 표시
  if (!products || products.length === 0) {
    console.log(`[Shopping API] 검색 결과 없음 - 선물: "${giftName}", 예산: ${giftPrice}`);
    return (
      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <h2 className="mb-4 text-xl font-semibold">추천 상품</h2>
        <div className="text-center py-8">
          <p className="text-muted-foreground">
            '{giftName}'에 대한 검색 결과를 찾을 수 없습니다.
          </p>
          <p className="mt-2 text-sm text-muted-foreground">
            다른 검색어로 시도해보세요.
          </p>
        </div>
      </div>
    );
  }

  console.log(`[Shopping API] 검색 성공 - 선물: "${giftName}", 결과: ${products.length}개`);

  // 예산 이하의 상품만 필터링
  const affordableProducts = products
    .filter(product => {
      const price = parseInt(product.lprice, 10);
      return price <= giftPrice;
    })
    .slice(0, 5); // 최대 5개까지만 표시

  console.log(`[Shopping API] 필터링 후 - 선물: "${giftName}", 예산 내 상품: ${affordableProducts.length}개`);

  const formatPrice = (price: string | number) => {
    const numPrice = typeof price === 'string' ? parseInt(price, 10) : price;
    return new Intl.NumberFormat("ko-KR", {
      style: "currency",
      currency: "KRW",
    }).format(numPrice);
  };

  // 예산 내 상품이 없으면 예산 초과 메시지 표시
  if (affordableProducts.length === 0) {
    console.log(`[Shopping API] 예산 초과 - 선물: "${giftName}", 예산: ${giftPrice}, 최저가: ${products[0]?.lprice}`);
    return (
      <div className="rounded-lg border bg-card p-6 shadow-sm">
        <h2 className="mb-4 text-xl font-semibold">추천 상품</h2>
        <div className="text-center py-8">
          <p className="text-muted-foreground">
            예산({formatPrice(giftPrice)}) 내 상품을 찾을 수 없습니다.
          </p>
          <p className="mt-2 text-sm text-muted-foreground">
            최저가: {formatPrice(products[0]?.lprice || 0)}
          </p>
        </div>
      </div>
    );
  }

  const cleanTitle = (title: string) => {
    return title.replace(/<[^>]*>/g, "").trim();
  };

  return (
    <div className="rounded-lg border bg-card p-6 shadow-sm">
      <h2 className="mb-4 text-xl font-semibold">추천 상품</h2>
      <p className="mb-4 text-sm text-muted-foreground">
        네이버 쇼핑에서 찾은 '{giftName}' 관련 상품입니다 (예산: {formatPrice(giftPrice.toString())} 이하)
      </p>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
        {affordableProducts.map((product, index) => (
          <a
            key={`${product.productId}-${index}`}
            href={product.link}
            target="_blank"
            rel="noopener noreferrer"
            className="group rounded-lg border bg-white p-4 transition-all hover:shadow-md"
          >
            <div className="aspect-square overflow-hidden rounded-md bg-gray-100">
              <img
                src={product.image}
                alt={cleanTitle(product.title)}
                className="h-full w-full object-cover transition-transform group-hover:scale-105"
                loading="lazy"
              />
            </div>

            <div className="mt-3 space-y-1">
              <h3 className="line-clamp-2 text-sm font-medium text-gray-900">
                {cleanTitle(product.title)}
              </h3>

              <p className="text-lg font-bold text-primary">
                {formatPrice(product.lprice)}
              </p>

              <p className="text-xs text-muted-foreground">{product.mallName}</p>
            </div>

            <div className="mt-3 flex items-center text-xs text-primary">
              <span>상품 보기</span>
              <span className="ml-1">→</span>
            </div>
          </a>
        ))}
      </div>
    </div>
  );
};
