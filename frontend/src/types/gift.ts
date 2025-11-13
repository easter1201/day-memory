export interface Gift {
  id: number;
  name: string;
  category: string;
  price: number;
  url?: string;
  memo?: string;
  isPurchased: boolean;
  eventId?: number;
  eventTitle?: string;
  createdAt: string;
  updatedAt: string;
}

export interface GiftsResponse {
  content: Gift[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface GiftsQueryParams {
  page?: number;
  size?: number;
  category?: string;
  isPurchased?: boolean;
  search?: string;
}

export interface CreateGiftRequest {
  name: string;
  category: string;
  price: number;
  url?: string;
  description?: string;
  eventId?: number;
}

export interface UpdateGiftRequest {
  name: string;
  category: string;
  price: number;
  url?: string;
  description?: string;
  eventId?: number;
}
