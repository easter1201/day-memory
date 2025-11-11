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

export interface DashboardStatistics {
  totalEvents: number;
  totalGifts: number;
  thisMonthEvents: number;
}

export interface DashboardData {
  statistics: DashboardStatistics;
  upcomingEvents: Event[];
}
