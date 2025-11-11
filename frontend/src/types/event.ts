export interface Event {
  id: number;
  title: string;
  eventDate: string;
  eventType: string;
  recipientName: string;
  relationship?: string;
  memo?: string;
  isTracked: boolean;
  reminders?: number[];
  createdAt: string;
  updatedAt: string;
}

export interface EventsResponse {
  content: Event[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface EventsQueryParams {
  page?: number;
  size?: number;
  filter?: "all" | "upcoming" | "past";
  search?: string;
}

export interface CreateEventRequest {
  title: string;
  eventDate: string;
  eventType: string;
  recipientName: string;
  relationship?: string;
  memo?: string;
  isTracked: boolean;
  reminders?: number[];
}

export interface UpdateEventRequest {
  title: string;
  eventDate: string;
  eventType: string;
  recipientName: string;
  relationship?: string;
  memo?: string;
  isTracked: boolean;
  reminders?: number[];
}
