# IA 문서 (Information Architecture) - Day Memory

## 📋 개요
Day Memory 프론트엔드의 전체 정보 구조를 정의하는 문서입니다. 페이지 계층 구조, 네비게이션 구조, URL 매핑을 포함합니다.

---

## 🗺️ 사이트맵 구조

### 1. 공개 영역 (Public)
```
Home (/)
├── 로그인 (/login)
├── 회원가입 (/signup)
└── 비밀번호 찾기 (/password-reset)
```

### 2. 인증 영역 (Authenticated)
```
대시보드 (/dashboard)
├── 이벤트 관리 (/events)
│   ├── 이벤트 목록 (/events)
│   ├── 이벤트 생성 (/events/new)
│   ├── 이벤트 상세 (/events/:id)
│   └── 이벤트 수정 (/events/:id/edit)
│
├── 선물 관리 (/gifts)
│   ├── 선물 목록 (/gifts)
│   ├── 선물 추가 (/gifts/new)
│   ├── 선물 상세 (/gifts/:id)
│   └── 선물 수정 (/gifts/:id/edit)
│
├── AI 선물 추천 (/recommendations)
│   ├── 추천 요청 (/recommendations/new)
│   └── 추천 결과 (/recommendations/:id)
│
├── 리마인더 설정 (/reminders)
│   ├── 리마인더 목록 (/reminders)
│   └── 리마인더 로그 (/reminders/logs)
│
├── 캘린더 뷰 (/calendar)
│
└── 설정 (/settings)
    ├── 프로필 (/settings/profile)
    ├── 알림 설정 (/settings/notifications)
    └── 계정 관리 (/settings/account)
```

---

## 🧭 네비게이션 구조

### 메인 네비게이션 (Authenticated Users)
```
[대시보드](/dashboard)
[이벤트](/events)
[선물](/gifts)
[AI 추천](/recommendations)
[캘린더](/calendar)
[설정](/settings)
```

### 서브 네비게이션
#### 이벤트 페이지
- [전체 이벤트](/events)
- [다가오는 이벤트](/events?filter=upcoming)
- [지난 이벤트](/events?filter=past)
- [새 이벤트](/events/new)

#### 선물 페이지
- [전체 선물](/gifts)
- [카테고리별](/gifts?category=:category)
- [구매 완료](/gifts?purchased=true)
- [미구매](/gifts?purchased=false)

#### 설정 페이지
- [프로필](/settings/profile)
- [알림 설정](/settings/notifications)
- [계정 관리](/settings/account)

---

## 🔗 URL 매핑

### 공개 페이지
| URL | 페이지 | 설명 |
|-----|--------|------|
| `/` | Home | 랜딩 페이지 |
| `/login` | Login | 로그인 페이지 |
| `/signup` | Signup | 회원가입 페이지 |
| `/password-reset` | PasswordReset | 비밀번호 재설정 |

### 인증 필요 페이지
| URL | 페이지 | 설명 |
|-----|--------|------|
| `/dashboard` | Dashboard | 메인 대시보드 |
| `/events` | EventList | 이벤트 목록 |
| `/events/new` | EventCreate | 이벤트 생성 |
| `/events/:id` | EventDetail | 이벤트 상세 |
| `/events/:id/edit` | EventEdit | 이벤트 수정 |
| `/gifts` | GiftList | 선물 목록 |
| `/gifts/new` | GiftCreate | 선물 추가 |
| `/gifts/:id` | GiftDetail | 선물 상세 |
| `/gifts/:id/edit` | GiftEdit | 선물 수정 |
| `/recommendations` | RecommendationList | AI 추천 내역 |
| `/recommendations/new` | RecommendationRequest | AI 추천 요청 |
| `/recommendations/:id` | RecommendationDetail | 추천 결과 상세 |
| `/reminders` | ReminderList | 리마인더 목록 |
| `/reminders/logs` | ReminderLogs | 발송 내역 |
| `/calendar` | Calendar | 캘린더 뷰 |
| `/settings/profile` | ProfileSettings | 프로필 설정 |
| `/settings/notifications` | NotificationSettings | 알림 설정 |
| `/settings/account` | AccountSettings | 계정 관리 |

---

## 🔐 접근 권한

### Public Routes
- `/`
- `/login`
- `/signup`
- `/password-reset`

### Protected Routes (인증 필요)
- `/dashboard`
- `/events/*`
- `/gifts/*`
- `/recommendations/*`
- `/reminders/*`
- `/calendar`
- `/settings/*`

### Redirect Rules
- 비인증 사용자가 Protected Route 접근 시 → `/login`으로 리다이렉트
- 인증된 사용자가 `/login`, `/signup` 접근 시 → `/dashboard`로 리다이렉트

---

## 📱 브레드크럼 (Breadcrumb)

### 예시
```
대시보드 > 이벤트 > 이벤트 상세
대시보드 > 선물 > 선물 추가
대시보드 > AI 추천 > 추천 요청
대시보드 > 설정 > 프로필
```

---

## 🎯 주요 사용자 플로우

### 1. 이벤트 생성 플로우
```
/dashboard → /events → /events/new → (생성 완료) → /events/:id
```

### 2. 선물 추가 플로우
```
/dashboard → /gifts → /gifts/new → (추가 완료) → /gifts
```

### 3. AI 추천 요청 플로우
```
/dashboard → /recommendations/new → (추천 요청) → /recommendations/:id
```

### 4. 리마인더 확인 플로우
```
/dashboard → /reminders → /reminders/logs
```

---

**작성일**: 2025-01-11
**버전**: 1.0
**참고**: project_idea.md 기반 작성
