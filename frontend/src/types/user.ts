export interface User {
  id: number;
  email: string;
  nickname: string;
  profileImageUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateProfileRequest {
  nickname: string;
  profileImageUrl?: string;
}

export interface NotificationSettings {
  emailNotificationsEnabled: boolean;
  reminderTime: string; // HH:mm format
}

export interface UpdateNotificationSettingsRequest {
  emailNotificationsEnabled: boolean;
  reminderTime: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}
