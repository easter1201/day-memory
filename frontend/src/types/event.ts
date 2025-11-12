export interface Event {
  id: number;
  title: string;
  description?: string;
  eventDate: string;
  eventType: string;
  isRecurring: boolean;
  isActive: boolean;
  isTracking: boolean;
  dDay: number;
  reminders?: {
    id: number;
    daysBeforeEvent: number;
    isActive: boolean;
  }[];
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
  description?: string;
  eventDate: string;
  eventType: string;
  isRecurring?: boolean;
  isTracking?: boolean;
  reminderDays?: number[];
}

export interface UpdateEventRequest {
  title: string;
  description?: string;
  eventDate: string;
  eventType: string;
  isRecurring?: boolean;
  isTracking?: boolean;
  reminderDays?: number[];
}
