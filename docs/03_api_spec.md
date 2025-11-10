# REST API 명세서

## 개요

Day Memory 프로젝트의 REST API 엔드포인트를 상세히 정의한 문서입니다.

**Base URL**: `http://localhost:8080/api`

---

## 목차

1. [사용자 인증](#1-사용자-인증)
2. [이벤트 관리](#2-이벤트-관리)
3. [리마인더](#3-리마인더)
4. [선물 관리](#4-선물-관리)
5. [AI 추천](#5-ai-추천)
6. [대시보드](#6-대시보드)

---

## 1. 사용자 인증

### 1.1 회원가입

- **URL**: `POST /api/users/signup`
- **인증**: 불필요
- **요청 예시**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123!",
  "name": "홍길동"
}
```

- **응답 예시** (201 Created):
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "홍길동",
  "createdAt": "2025-01-10T10:30:00"
}
```

- **상태 코드**:
  - `201 Created`: 회원가입 성공
  - `400 Bad Request`: 잘못된 요청 (이메일 형식 오류 등)
  - `409 Conflict`: 이미 존재하는 이메일

---

### 1.2 로그인

- **URL**: `POST /api/users/login`
- **인증**: 불필요
- **요청 예시**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123!"
}
```

- **응답 예시** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "email": "user@example.com",
  "name": "홍길동"
}
```

- **상태 코드**:
  - `200 OK`: 로그인 성공
  - `400 Bad Request`: 잘못된 요청
  - `401 Unauthorized`: 이메일 또는 비밀번호 불일치

---

### 1.3 토큰 재발급

- **URL**: `POST /api/users/refresh`
- **인증**: Refresh Token 필요
- **요청 예시**:
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

- **응답 예시** (200 OK):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

- **상태 코드**:
  - `200 OK`: 토큰 재발급 성공
  - `401 Unauthorized`: 유효하지 않은 Refresh Token

---

## 2. 이벤트 관리

### 2.1 이벤트 생성

- **URL**: `POST /api/events?userId={userId}`
- **인증**: JWT Access Token 필요
- **요청 예시**:
```json
{
  "title": "발렌타인데이",
  "description": "2025년 발렌타인데이",
  "eventDate": "2025-02-14",
  "eventType": "VALENTINES_DAY",
  "isRecurring": true,
  "isTracking": true,
  "reminderDays": [30, 14, 7, 3, 1]
}
```

- **응답 예시** (200 OK):
```json
{
  "id": 1,
  "title": "발렌타인데이",
  "description": "2025년 발렌타인데이",
  "eventDate": "2025-02-14",
  "eventType": "VALENTINES_DAY",
  "isRecurring": true,
  "isActive": true,
  "isTracking": true,
  "dDay": 35,
  "reminders": [
    {"id": 1, "daysBeforeEvent": 30, "isActive": true},
    {"id": 2, "daysBeforeEvent": 14, "isActive": true},
    {"id": 3, "daysBeforeEvent": 7, "isActive": true},
    {"id": 4, "daysBeforeEvent": 3, "isActive": true},
    {"id": 5, "daysBeforeEvent": 1, "isActive": true}
  ]
}
```

- **상태 코드**:
  - `200 OK`: 이벤트 생성 성공
  - `400 Bad Request`: 잘못된 요청
  - `401 Unauthorized`: 인증 실패

---

### 2.2 이벤트 목록 조회

- **URL**: `GET /api/events?userId={userId}`
- **인증**: JWT Access Token 필요
- **응답 예시** (200 OK):
```json
[
  {
    "id": 1,
    "title": "발렌타인데이",
    "eventDate": "2025-02-14",
    "eventType": "VALENTINES_DAY",
    "isTracking": true,
    "dDay": 35,
    "reminders": [...]
  },
  {
    "id": 2,
    "title": "어머니 생신",
    "eventDate": "2025-03-15",
    "eventType": "BIRTHDAY",
    "isTracking": true,
    "dDay": 64,
    "reminders": [...]
  }
]
```

- **상태 코드**:
  - `200 OK`: 조회 성공
  - `401 Unauthorized`: 인증 실패

---

### 2.3 특정 이벤트 조회

- **URL**: `GET /api/events/{eventId}`
- **인증**: JWT Access Token 필요
- **응답 예시** (200 OK):
```json
{
  "id": 1,
  "title": "발렌타인데이",
  "description": "2025년 발렌타인데이",
  "eventDate": "2025-02-14",
  "eventType": "VALENTINES_DAY",
  "isRecurring": true,
  "isActive": true,
  "isTracking": true,
  "dDay": 35,
  "reminders": [
    {"id": 1, "daysBeforeEvent": 30, "isActive": true},
    {"id": 2, "daysBeforeEvent": 7, "isActive": true}
  ]
}
```

- **상태 코드**:
  - `200 OK`: 조회 성공
  - `401 Unauthorized`: 인증 실패
  - `404 Not Found`: 이벤트를 찾을 수 없음

---

### 2.4 이벤트 수정

- **URL**: `PUT /api/events/{eventId}`
- **인증**: JWT Access Token 필요
- **요청 예시**:
```json
{
  "title": "발렌타인데이 (수정)",
  "description": "초콜릿 선물하기",
  "eventDate": "2025-02-14",
  "eventType": "VALENTINES_DAY",
  "isRecurring": true,
  "isTracking": true,
  "reminderDays": [30, 14, 7, 1]
}
```

- **응답 예시** (200 OK):
```json
{
  "id": 1,
  "title": "발렌타인데이 (수정)",
  "description": "초콜릿 선물하기",
  "eventDate": "2025-02-14",
  "dDay": 35,
  "reminders": [...]
}
```

- **상태 코드**:
  - `200 OK`: 수정 성공
  - `400 Bad Request`: 잘못된 요청
  - `401 Unauthorized`: 인증 실패
  - `404 Not Found`: 이벤트를 찾을 수 없음

---

### 2.5 이벤트 삭제 (비활성화)

- **URL**: `DELETE /api/events/{eventId}`
- **인증**: JWT Access Token 필요
- **응답**: 204 No Content

- **상태 코드**:
  - `204 No Content`: 삭제 성공
  - `401 Unauthorized`: 인증 실패
  - `404 Not Found`: 이벤트를 찾을 수 없음

---

### 2.6 다가오는 이벤트 조회

- **URL**: `GET /api/events/upcoming?userId={userId}&days={days}`
- **인증**: JWT Access Token 필요
- **Query Parameters**:
  - `userId` (required): 사용자 ID
  - `days` (optional, default=30): 몇 일 이내의 이벤트

- **응답 예시** (200 OK):
```json
[
  {
    "id": 1,
    "title": "발렌타인데이",
    "eventDate": "2025-02-14",
    "dDay": 15,
    "eventType": "VALENTINES_DAY"
  }
]
```

- **상태 코드**:
  - `200 OK`: 조회 성공
  - `401 Unauthorized`: 인증 실패

---

### 2.7 이벤트 추적 토글

- **URL**: `PUT /api/events/{eventId}/tracking`
- **인증**: JWT Access Token 필요
- **요청 예시**:
```json
{
  "isTracking": false
}
```

- **응답 예시** (200 OK):
```json
{
  "id": 1,
  "title": "발렌타인데이",
  "isTracking": false
}
```

- **상태 코드**:
  - `200 OK`: 변경 성공
  - `401 Unauthorized`: 인증 실패
  - `404 Not Found`: 이벤트를 찾을 수 없음

---

## 3. 리마인더

### 3.1 리마인더 설정 수정

- **URL**: `PUT /api/events/{eventId}/reminders`
- **인증**: JWT Access Token 필요
- **요청 예시**:
```json
{
  "reminderDays": [60, 30, 14, 7, 3, 1]
}
```

- **응답 예시** (200 OK):
```json
{
  "id": 1,
  "title": "발렌타인데이",
  "reminders": [
    {"id": 6, "daysBeforeEvent": 60, "isActive": true},
    {"id": 7, "daysBeforeEvent": 30, "isActive": true},
    {"id": 8, "daysBeforeEvent": 14, "isActive": true},
    {"id": 9, "daysBeforeEvent": 7, "isActive": true},
    {"id": 10, "daysBeforeEvent": 3, "isActive": true},
    {"id": 11, "daysBeforeEvent": 1, "isActive": true}
  ]
}
```

- **상태 코드**:
  - `200 OK`: 수정 성공
  - `400 Bad Request`: 잘못된 요청
  - `401 Unauthorized`: 인증 실패
  - `404 Not Found`: 이벤트를 찾을 수 없음

---

## 4. 선물 관리

### 4.1 선물 아이템 생성

- **URL**: `POST /api/gifts?userId={userId}`
- **인증**: JWT Access Token 필요
- **요청 예시**:
```json
{
  "name": "초콜릿 세트",
  "description": "고디바 초콜릿",
  "price": 50000,
  "url": "https://example.com/product/123",
  "category": "FOOD",
  "eventId": 1
}
```

- **응답 예시** (200 OK):
```json
{
  "id": 1,
  "name": "초콜릿 세트",
  "description": "고디바 초콜릿",
  "price": 50000,
  "url": "https://example.com/product/123",
  "category": "FOOD",
  "isPurchased": false,
  "eventId": 1
}
```

- **상태 코드**:
  - `200 OK`: 생성 성공
  - `400 Bad Request`: 잘못된 요청
  - `401 Unauthorized`: 인증 실패

---

### 4.2 선물 목록 조회

- **URL**: `GET /api/gifts?userId={userId}`
- **인증**: JWT Access Token 필요
- **응답 예시** (200 OK):
```json
[
  {
    "id": 1,
    "name": "초콜릿 세트",
    "price": 50000,
    "category": "FOOD",
    "isPurchased": false,
    "eventId": 1
  },
  {
    "id": 2,
    "name": "장미 꽃다발",
    "price": 80000,
    "category": "FLOWER",
    "isPurchased": false,
    "eventId": 1
  }
]
```

- **상태 코드**:
  - `200 OK`: 조회 성공
  - `401 Unauthorized`: 인증 실패

---

### 4.3 특정 이벤트의 선물 조회

- **URL**: `GET /api/gifts/event/{eventId}`
- **인증**: JWT Access Token 필요
- **응답 예시** (200 OK):
```json
[
  {
    "id": 1,
    "name": "초콜릿 세트",
    "category": "FOOD",
    "isPurchased": false,
    "eventId": 1
  }
]
```

- **상태 코드**:
  - `200 OK`: 조회 성공
  - `401 Unauthorized`: 인증 실패

---

### 4.4 선물 구매 완료 표시

- **URL**: `PUT /api/gifts/{giftId}/purchase`
- **인증**: JWT Access Token 필요
- **응답 예시** (200 OK):
```json
{
  "id": 1,
  "name": "초콜릿 세트",
  "isPurchased": true
}
```

- **상태 코드**:
  - `200 OK`: 변경 성공
  - `401 Unauthorized`: 인증 실패
  - `404 Not Found`: 선물을 찾을 수 없음

---

## 5. AI 추천

### 5.1 AI 선물 추천 요청

- **URL**: `POST /api/gifts/recommend`
- **인증**: JWT Access Token 필요
- **요청 예시**:
```json
{
  "eventId": 1,
  "budget": 100000,
  "relationship": "연인",
  "preferences": ["꽃", "초콜릿"]
}
```

- **응답 예시** (200 OK):
```json
{
  "userSavedGifts": [
    {
      "id": 1,
      "name": "초콜릿 세트",
      "price": 50000,
      "category": "FOOD"
    }
  ],
  "aiRecommendations": [
    {
      "name": "장미 꽃다발 + 초콜릿 세트",
      "estimatedPrice": 95000,
      "reason": "발렌타인데이에 가장 인기 있는 조합이며, 예산 내에서 로맨틱한 분위기를 연출할 수 있습니다.",
      "category": "FLOWER"
    },
    {
      "name": "프리미엄 초콜릿 세트",
      "estimatedPrice": 80000,
      "reason": "고급스러운 패키지로 특별한 날을 기념하기 좋습니다.",
      "category": "FOOD"
    }
  ]
}
```

- **상태 코드**:
  - `200 OK`: 추천 성공
  - `400 Bad Request`: 잘못된 요청
  - `401 Unauthorized`: 인증 실패
  - `500 Internal Server Error`: AI API 호출 실패

---

## 6. 대시보드

### 6.1 대시보드 요약 정보

- **URL**: `GET /api/dashboard?userId={userId}`
- **인증**: JWT Access Token 필요
- **응답 예시** (200 OK):
```json
{
  "upcomingEvents": [
    {
      "id": 1,
      "title": "발렌타인데이",
      "eventDate": "2025-02-14",
      "dDay": 35
    }
  ],
  "unpurchasedGiftsCount": 3,
  "recentReminders": [
    {
      "eventTitle": "발렌타인데이",
      "sentAt": "2025-01-10T09:00:00",
      "status": "SENT"
    }
  ],
  "totalEvents": 5,
  "trackedEvents": 4
}
```

- **상태 코드**:
  - `200 OK`: 조회 성공
  - `401 Unauthorized`: 인증 실패

---

## 공통 에러 응답

모든 API에서 공통적으로 사용하는 에러 응답 형식입니다.

### 400 Bad Request
```json
{
  "status": 400,
  "code": "INVALID_INPUT",
  "message": "입력값이 올바르지 않습니다.",
  "timestamp": "2025-01-10T10:30:00"
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "인증에 실패했습니다.",
  "timestamp": "2025-01-10T10:30:00"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "code": "EVENT_NOT_FOUND",
  "message": "해당 이벤트를 찾을 수 없습니다.",
  "timestamp": "2025-01-10T10:30:00"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "code": "INTERNAL_SERVER_ERROR",
  "message": "서버 내부 오류가 발생했습니다.",
  "timestamp": "2025-01-10T10:30:00"
}
```
