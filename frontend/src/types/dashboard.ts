export interface Event {
  id: number;
  title: string;
  eventDate: string;
  eventType: string;
  recipientName: string;
  createdAt: string;
}

export interface GiftItem {
  id: number;
  name: string;
  price: number;
  url?: string;
  memo?: string;
  createdAt: string;
}

export interface ReminderStatus {
  sentCount: number;
  failedCount: number;
  lastSentAt: string;
}

export interface DashboardData {
  upcomingEventsCount: number;
  unpurchasedGiftsCount: number;
  recentReminderStatus: ReminderStatus;
  thisMonthEventsCount: number;
  upcomingEvents: Event[];
}
