export interface RecommendationRequest {
  eventId: number;
  budget: number;
  preferredCategories: string[];
  recipientGender?: string;
  recipientAge?: number;
  additionalMessage?: string;
}

export interface RecommendedGift {
  id: number;
  name: string;
  category: string;
  estimatedPrice: number;
  reason: string;
  url?: string;
  imageUrl?: string;
}

export interface UserSavedGift {
  id: number;
  name: string;
  category: string;
  price: number;
  isPurchased: boolean;
}

export interface Recommendation {
  id: number;
  eventId: number;
  eventTitle: string;
  recipientName: string;
  budget: number;
  preferredCategories: string[];
  recipientGender?: string;
  recipientAge?: number;
  additionalMessage?: string;
  status: "PENDING" | "COMPLETED" | "FAILED";
  userSavedGifts: UserSavedGift[];
  aiRecommendations: RecommendedGift[];
  createdAt: string;
  completedAt?: string;
}

export interface RecommendationsResponse {
  content: Recommendation[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface RecommendationsQueryParams {
  page?: number;
  size?: number;
  status?: "PENDING" | "COMPLETED" | "FAILED";
}

export interface SaveRecommendedGiftRequest {
  recommendationId: number;
  recommendedGiftId: number;
}
