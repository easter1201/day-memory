// 이벤트 타입 목록
export const EVENT_TYPES = [
  { value: "BIRTHDAY", label: "생일" },
  { value: "ANNIVERSARY", label: "기념일" },
  { value: "HOLIDAY", label: "명절" },
  { value: "OTHER", label: "기타" },
] as const;

export type EventType = (typeof EVENT_TYPES)[number]["value"];

// 선물 카테고리 목록
export const GIFT_CATEGORIES = [
  { value: "ELECTRONICS", label: "전자기기" },
  { value: "FASHION", label: "패션" },
  { value: "FOOD", label: "식품" },
  { value: "BOOK", label: "도서" },
  { value: "HOBBY", label: "취미" },
  { value: "BEAUTY", label: "뷰티" },
  { value: "HOME", label: "생활용품" },
  { value: "OTHER", label: "기타" },
] as const;

export type GiftCategory = (typeof GIFT_CATEGORIES)[number]["value"];

// 관계 목록
export const RELATIONSHIPS = [
  { value: "FAMILY", label: "가족" },
  { value: "FRIEND", label: "친구" },
  { value: "COLLEAGUE", label: "동료" },
  { value: "ACQUAINTANCE", label: "지인" },
  { value: "OTHER", label: "기타" },
] as const;

export type Relationship = (typeof RELATIONSHIPS)[number]["value"];

// 알림 시간 옵션 (일 단위)
export const REMINDER_OPTIONS = [
  { value: 1, label: "1일 전" },
  { value: 3, label: "3일 전" },
  { value: 7, label: "7일 전" },
  { value: 14, label: "14일 전" },
  { value: 30, label: "30일 전" },
] as const;

// 페이지네이션 기본값
export const PAGINATION = {
  DEFAULT_PAGE: 0,
  DEFAULT_SIZE: 12,
  SIZE_OPTIONS: [12, 24, 48, 96],
} as const;

// 이벤트 타입별 색상 매핑
export const EVENT_TYPE_COLORS: Record<EventType, string> = {
  BIRTHDAY: "bg-blue-100 text-blue-800",
  ANNIVERSARY: "bg-pink-100 text-pink-800",
  HOLIDAY: "bg-green-100 text-green-800",
  OTHER: "bg-gray-100 text-gray-800",
};

// 선물 카테고리별 색상 매핑
export const GIFT_CATEGORY_COLORS: Record<GiftCategory, string> = {
  ELECTRONICS: "bg-blue-100 text-blue-800",
  FASHION: "bg-purple-100 text-purple-800",
  FOOD: "bg-orange-100 text-orange-800",
  BOOK: "bg-green-100 text-green-800",
  HOBBY: "bg-pink-100 text-pink-800",
  BEAUTY: "bg-rose-100 text-rose-800",
  HOME: "bg-amber-100 text-amber-800",
  OTHER: "bg-gray-100 text-gray-800",
};

// 날짜 형식
export const DATE_FORMATS = {
  DISPLAY: "YYYY년 MM월 DD일",
  INPUT: "YYYY-MM-DD",
  ISO: "YYYY-MM-DDTHH:mm:ss",
} as const;

// API 엔드포인트 (참고용)
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: "/auth/login",
    SIGNUP: "/auth/signup",
    LOGOUT: "/auth/logout",
    PASSWORD_RESET: "/auth/password-reset",
  },
  EVENTS: {
    LIST: "/events",
    DETAIL: (id: number) => `/events/${id}`,
    CREATE: "/events",
    UPDATE: (id: number) => `/events/${id}`,
    DELETE: (id: number) => `/events/${id}`,
  },
  GIFTS: {
    LIST: "/gifts",
    DETAIL: (id: number) => `/gifts/${id}`,
    CREATE: "/gifts",
    UPDATE: (id: number) => `/gifts/${id}`,
    DELETE: (id: number) => `/gifts/${id}`,
    TOGGLE_PURCHASED: (id: number) => `/gifts/${id}/toggle-purchased`,
  },
  DASHBOARD: "/dashboard",
} as const;

// 로컬 스토리지 키
export const STORAGE_KEYS = {
  ACCESS_TOKEN: "accessToken",
  REFRESH_TOKEN: "refreshToken",
  THEME: "theme",
  SIDEBAR_OPEN: "sidebarOpen",
} as const;

// 에러 메시지
export const ERROR_MESSAGES = {
  NETWORK_ERROR: "네트워크 연결을 확인해주세요",
  UNAUTHORIZED: "로그인이 필요합니다",
  FORBIDDEN: "접근 권한이 없습니다",
  NOT_FOUND: "요청한 리소스를 찾을 수 없습니다",
  SERVER_ERROR: "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요",
  VALIDATION_ERROR: "입력값을 확인해주세요",
  UNKNOWN_ERROR: "알 수 없는 오류가 발생했습니다",
} as const;

// 성공 메시지
export const SUCCESS_MESSAGES = {
  LOGIN: "로그인되었습니다",
  LOGOUT: "로그아웃되었습니다",
  SIGNUP: "회원가입이 완료되었습니다",
  EVENT_CREATED: "이벤트가 생성되었습니다",
  EVENT_UPDATED: "이벤트가 수정되었습니다",
  EVENT_DELETED: "이벤트가 삭제되었습니다",
  GIFT_CREATED: "선물이 추가되었습니다",
  GIFT_UPDATED: "선물이 수정되었습니다",
  GIFT_DELETED: "선물이 삭제되었습니다",
  PASSWORD_RESET_SENT: "비밀번호 재설정 이메일이 발송되었습니다",
} as const;
