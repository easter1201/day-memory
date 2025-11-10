export enum EventType {
  ANNIVERSARY = 'ANNIVERSARY',
  BIRTHDAY = 'BIRTHDAY',
  HOLIDAY = 'HOLIDAY',
  CUSTOM = 'CUSTOM',
}

export enum GiftCategory {
  FLOWER = 'FLOWER',
  JEWELRY = 'JEWELRY',
  COSMETICS = 'COSMETICS',
  FASHION = 'FASHION',
  ELECTRONICS = 'ELECTRONICS',
  FOOD = 'FOOD',
  EXPERIENCE = 'EXPERIENCE',
  BOOK = 'BOOK',
  HOBBY = 'HOBBY',
  OTHER = 'OTHER',
}

export interface Event {
  id: number;
  title: string;
  description: string;
  eventDate: string;
  eventType: EventType;
  isRecurring: boolean;
  isActive: boolean;
  dDay: number;
  remindAt30Days: boolean;
  remindAt7Days: boolean;
  remindAt1Day: boolean;
}

export interface EventRequest {
  title: string;
  description: string;
  eventDate: string;
  eventType: EventType;
  isRecurring: boolean;
  remindAt30Days?: boolean;
  remindAt7Days?: boolean;
  remindAt1Day?: boolean;
}

export interface GiftItem {
  id: number;
  eventId: number | null;
  name: string;
  description: string;
  price: number;
  url: string;
  category: GiftCategory;
  isPurchased: boolean;
}

export interface GiftItemRequest {
  eventId?: number;
  name: string;
  description: string;
  price?: number;
  url?: string;
  category: GiftCategory;
}
