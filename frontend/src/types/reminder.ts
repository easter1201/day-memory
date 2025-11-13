export interface Reminder {
  id: number;
  eventId: number;
  eventTitle: string;
  recipientName: string;
  eventDate: string;
  daysBeforeEvent: number;
  reminderDate: string;
  status: "PENDING" | "SENT" | "FAILED";
  createdAt: string;
}

export interface ReminderLog {
  id: number;
  reminderId: number;
  eventTitle: string;
  recipientName: string;
  eventDate: string;
  sentAt: string;
  status: "SUCCESS" | "FAILED";
  errorMessage?: string;
  retryCount: number;
}

export interface RemindersResponse {
  content: Reminder[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface ReminderLogsResponse {
  content: ReminderLog[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface RemindersQueryParams {
  page?: number;
  size?: number;
  status?: "PENDING" | "SENT" | "FAILED";
}

export interface ReminderLogsQueryParams {
  page?: number;
  size?: number;
  status?: "SUCCESS" | "FAILED";
}

export interface GlobalReminderSettings {
  enabled: boolean;
  defaultDaysBefore: number[];
  notificationMethod: "EMAIL" | "PUSH" | "BOTH";
}

export interface UpdateGlobalReminderSettingsRequest {
  enabled: boolean;
  defaultDaysBefore: number[];
  notificationMethod: "EMAIL" | "PUSH" | "BOTH";
}

export interface RetryReminderRequest {
  logId: number;
}
