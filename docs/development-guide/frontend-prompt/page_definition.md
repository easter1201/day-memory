# 페이지 정의서 (Page Definition) - Day Memory

## 📋 개요
각 페이지의 URL, 설명, 주요 섹션을 정의하는 문서입니다. 페이지별 레이아웃 및 포함 요소를 명확히 하여 퍼블리싱 구조를 일관되게 유지합니다.

---

## 🌐 공개 페이지

### PAGE_HOME
- **URL**: `/`
- **설명**: 랜딩 페이지 (Day Memory 소개 및 로그인 유도)
- **주요 섹션**:
  - `Header` (로고, 로그인/회원가입 버튼)
  - `HeroSection` (메인 타이틀, 서브 타이틀, CTA 버튼)
  - `FeaturesSection` (주요 기능 3가지 소개)
  - `Footer` (저작권 정보)

### PAGE_LOGIN
- **URL**: `/login`
- **설명**: 로그인 페이지
- **주요 섹션**:
  - `Header` (로고)
  - `LoginForm` (이메일, 비밀번호, 로그인 버튼)
  - `SocialLoginButtons` (Google, Kakao 로그인 - 선택)
  - `LinkToSignup` (회원가입 링크)
  - `LinkToPasswordReset` (비밀번호 찾기 링크)
  - `Footer`

### PAGE_SIGNUP
- **URL**: `/signup`
- **설명**: 회원가입 페이지
- **주요 섹션**:
  - `Header` (로고)
  - `SignupForm` (이메일, 비밀번호, 비밀번호 확인, 닉네임, 가입 버튼)
  - `LinkToLogin` (로그인 링크)
  - `Footer`

### PAGE_PASSWORD_RESET
- **URL**: `/password-reset`
- **설명**: 비밀번호 재설정 페이지
- **주요 섹션**:
  - `Header` (로고)
  - `PasswordResetForm` (이메일 입력, 인증 메일 발송 버튼)
  - `LinkToLogin` (로그인 링크)
  - `Footer`

---

## 🔐 인증 필요 페이지

### PAGE_DASHBOARD
- **URL**: `/dashboard`
- **설명**: 메인 대시보드 (다가오는 이벤트, 통계, 빠른 액션)
- **주요 섹션**:
  - `Header` (로고, 네비게이션 메뉴, 프로필 드롭다운)
  - `Sidebar` (메인 네비게이션)
  - `WelcomeBanner` (사용자 이름, 인사 메시지)
  - `UpcomingEventsWidget` (향후 30일 이내 이벤트 5개)
  - `StatisticsCards` (총 이벤트 수, 총 선물 수, 이번 달 이벤트 수)
  - `QuickActionButtons` (새 이벤트 추가, 새 선물 추가, AI 추천 요청)
  - `Footer`

---

## 📅 이벤트 관리 페이지

### PAGE_EVENT_LIST
- **URL**: `/events`
- **설명**: 이벤트 목록 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("이벤트 관리")
  - `FilterTabs` (전체, 다가오는 이벤트, 지난 이벤트)
  - `SearchBar` (이벤트명 검색)
  - `EventCardGrid` (이벤트 카드 리스트)
  - `Pagination`
  - `CreateEventButton` (플로팅 버튼)
  - `Footer`

### PAGE_EVENT_CREATE
- **URL**: `/events/new`
- **설명**: 이벤트 생성 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("새 이벤트 추가")
  - `EventForm` (이벤트명, 날짜, 타입, 대상자, 관계, 메모, 추적 여부, 리마인더 설정)
  - `SubmitButton` (저장)
  - `CancelButton` (취소)
  - `Footer`

### PAGE_EVENT_DETAIL
- **URL**: `/events/:id`
- **설명**: 이벤트 상세 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `EventHeader` (이벤트명, D-Day 카운터)
  - `EventInfoSection` (날짜, 타입, 대상자, 관계, 메모)
  - `ReminderSection` (설정된 리마인더 목록)
  - `LinkedGiftsSection` (연결된 선물 목록)
  - `EditButton` (수정)
  - `DeleteButton` (삭제)
  - `Footer`

### PAGE_EVENT_EDIT
- **URL**: `/events/:id/edit`
- **설명**: 이벤트 수정 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("이벤트 수정")
  - `EventForm` (기존 데이터 미리 채워짐)
  - `SubmitButton` (저장)
  - `CancelButton` (취소)
  - `Footer`

---

## 🎁 선물 관리 페이지

### PAGE_GIFT_LIST
- **URL**: `/gifts`
- **설명**: 선물 목록 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("선물 관리")
  - `FilterButtons` (전체, 카테고리별, 구매 완료, 미구매)
  - `SearchBar` (선물명 검색)
  - `GiftCardGrid` (선물 카드 리스트)
  - `Pagination`
  - `AddGiftButton` (플로팅 버튼)
  - `Footer`

### PAGE_GIFT_CREATE
- **URL**: `/gifts/new`
- **설명**: 선물 추가 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("새 선물 추가")
  - `GiftForm` (선물명, 카테고리, 가격, URL, 메모, 구매 여부, 연결 이벤트 선택)
  - `SubmitButton` (저장)
  - `CancelButton` (취소)
  - `Footer`

### PAGE_GIFT_DETAIL
- **URL**: `/gifts/:id`
- **설명**: 선물 상세 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `GiftHeader` (선물명, 카테고리)
  - `GiftInfoSection` (가격, URL, 메모, 구매 여부)
  - `LinkedEventSection` (연결된 이벤트 정보)
  - `PurchaseToggleButton` (구매 완료 토글)
  - `EditButton` (수정)
  - `DeleteButton` (삭제)
  - `Footer`

### PAGE_GIFT_EDIT
- **URL**: `/gifts/:id/edit`
- **설명**: 선물 수정 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("선물 수정")
  - `GiftForm` (기존 데이터 미리 채워짐)
  - `SubmitButton` (저장)
  - `CancelButton` (취소)
  - `Footer`

---

## 🤖 AI 추천 페이지

### PAGE_RECOMMENDATION_LIST
- **URL**: `/recommendations`
- **설명**: AI 추천 내역 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("AI 추천 내역")
  - `RecommendationCardList` (과거 추천 요청 목록)
  - `NewRecommendationButton`
  - `Footer`

### PAGE_RECOMMENDATION_REQUEST
- **URL**: `/recommendations/new`
- **설명**: AI 추천 요청 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("AI 선물 추천 요청")
  - `RecommendationForm` (이벤트 선택, 예산 입력, 선호 카테고리, 추가 메시지)
  - `SubmitButton` (추천 요청)
  - `CancelButton` (취소)
  - `Footer`

### PAGE_RECOMMENDATION_DETAIL
- **URL**: `/recommendations/:id`
- **설명**: AI 추천 결과 상세 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("AI 추천 결과")
  - `RequestInfoSection` (요청 정보: 이벤트, 예산, 카테고리)
  - `UserSavedGiftsSection` (사용자 저장 선물 우선 표시)
  - `AIRecommendationsSection` (AI 추천 선물 목록 + 추천 이유)
  - `SaveToGiftListButton` (선물 리스트에 저장)
  - `Footer`

---

## ⏰ 리마인더 페이지

### PAGE_REMINDER_LIST
- **URL**: `/reminders`
- **설명**: 리마인더 목록 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("리마인더 설정")
  - `ReminderCardList` (이벤트별 리마인더 설정 목록)
  - `GlobalReminderSettings` (전역 리마인더 일수 설정)
  - `Footer`

### PAGE_REMINDER_LOGS
- **URL**: `/reminders/logs`
- **설명**: 리마인더 발송 내역 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("발송 내역")
  - `FilterButtons` (전체, 성공, 실패)
  - `ReminderLogTable` (날짜, 이벤트명, 수신자, 상태, 재시도)
  - `Pagination`
  - `Footer`

---

## 📆 캘린더 페이지

### PAGE_CALENDAR
- **URL**: `/calendar`
- **설명**: 월별 캘린더 뷰
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `PageTitle` ("캘린더")
  - `CalendarView` (월별 캘린더, 이벤트 마커)
  - `EventPopup` (날짜 클릭 시 해당 날짜의 이벤트 목록)
  - `MonthNavigationButtons`
  - `Footer`

---

## ⚙️ 설정 페이지

### PAGE_SETTINGS_PROFILE
- **URL**: `/settings/profile`
- **설명**: 프로필 설정 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `SettingsTabs` (프로필, 알림, 계정)
  - `ProfileForm` (프로필 사진, 닉네임, 이메일)
  - `SaveButton`
  - `Footer`

### PAGE_SETTINGS_NOTIFICATIONS
- **URL**: `/settings/notifications`
- **설명**: 알림 설정 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `SettingsTabs`
  - `NotificationSettings` (이메일 알림 on/off, 리마인더 발송 시간)
  - `SaveButton`
  - `Footer`

### PAGE_SETTINGS_ACCOUNT
- **URL**: `/settings/account`
- **설명**: 계정 관리 페이지
- **주요 섹션**:
  - `Header`
  - `Sidebar`
  - `SettingsTabs`
  - `PasswordChangeForm` (현재 비밀번호, 새 비밀번호, 확인)
  - `LogoutButton`
  - `DeleteAccountButton`
  - `Footer`

---

## 📊 공통 섹션 정의

### Header (모든 인증 페이지)
- 로고
- 네비게이션 메뉴
- 프로필 드롭다운 (설정, 로그아웃)

### Sidebar (모든 인증 페이지)
- 대시보드 링크
- 이벤트 링크
- 선물 링크
- AI 추천 링크
- 캘린더 링크
- 설정 링크

### Footer (모든 페이지)
- 저작권 정보
- 프로젝트 정보 링크

---

**작성일**: 2025-01-11
**버전**: 1.0
**참고**: project_idea.md, ia_structure.md 기반 작성
