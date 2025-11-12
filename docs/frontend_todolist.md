# 프론트엔드 개발 계획 (Frontend Todolist) - Day Memory

## 📋 개요
Day Memory 프론트엔드 개발 계획 및 진행 상황을 추적하는 문서입니다. 모든 페이지, 기능, 컴포넌트의 개발 우선순위와 상태를 관리합니다.

---

## 📊 전체 진행 상황

### Phase별 진행률
- **Phase 1 (MVP)**: 221/221 (100%) ✅
- **Phase 2 (확장 기능)**: 65/65 (100%) ✅
- **Phase 3 (고도화)**: 13/15 (86.7%)
- **전체**: 299/301 (99.3%)

---

## 🎯 Phase 1: MVP (Core Features)

### 1. 프로젝트 초기 설정
- [x] Vite + React + TypeScript 프로젝트 생성
- [x] TailwindCSS 3 설치 및 설정
- [x] shadcn/ui 초기화 및 테마 설정
- [x] Redux Toolkit + RTK Query 설정
- [x] React Router v6 설정
- [x] ESLint + Prettier 설정
- [x] Git 저장소 설정 및 .gitignore 구성
- [x] 환경 변수 설정 (.env.example 작성)

### 2. 공통 레이아웃 및 네비게이션 (우선순위: 높음)
- [x] Header 컴포넌트 구현
  - [x] 로고
  - [x] 네비게이션 메뉴
  - [x] 프로필 드롭다운
- [x] Sidebar 컴포넌트 구현
  - [x] 메인 네비게이션 링크
  - [x] 활성 상태 표시
  - [x] 반응형 대응 (모바일 토글)
- [x] Footer 컴포넌트 구현
- [x] PageLayout 컴포넌트 구현 (Header + Sidebar + Content 조합)
- [x] AuthLayout 컴포넌트 구현 (인증 페이지용)

### 3. 인증 페이지 (우선순위: 높음)
#### 3.1 로그인 페이지 (/)
- [x] 페이지 레이아웃 구성
- [x] 로그인 폼 구현
  - [x] 이메일 입력 필드
  - [x] 비밀번호 입력 필드
  - [x] 유효성 검증 (React Hook Form + Zod)
  - [x] 로그인 버튼
- [x] API 연동 (authApi.login)
- [x] JWT 토큰 로컬스토리지 저장
- [x] 로그인 성공 시 /dashboard 리다이렉트
- [x] 에러 처리 (토스트 메시지)
- [x] "회원가입" 링크
- [x] "비밀번호 찾기" 링크

#### 3.2 회원가입 페이지 (/signup)
- [x] 페이지 레이아웃 구성
- [x] 회원가입 폼 구현
  - [x] 이메일 입력 필드
  - [x] 비밀번호 입력 필드
  - [x] 비밀번호 확인 필드
  - [x] 닉네임 입력 필드
  - [x] 유효성 검증 (이메일 형식, 비밀번호 강도, 일치 여부)
  - [x] 회원가입 버튼
- [x] API 연동 (authApi.signup)
- [x] 가입 성공 시 /login 리다이렉트
- [x] 에러 처리 (이미 존재하는 이메일 등)

#### 3.3 비밀번호 재설정 페이지 (/password-reset)
- [x] 페이지 레이아웃 구성
- [x] 이메일 입력 폼 구현
- [x] API 연동 (authApi.passwordReset)
- [x] 성공 메시지 표시

### 4. 대시보드 페이지 (/dashboard) (우선순위: 높음)
- [x] 페이지 레이아웃 구성
- [x] WelcomeBanner 컴포넌트
  - [x] 사용자 이름 표시
  - [x] 인사 메시지
- [x] UpcomingEventsWidget 컴포넌트
  - [x] 향후 30일 이내 이벤트 5개 표시
  - [x] D-Day 계산 및 표시
  - [x] 이벤트 클릭 시 상세 페이지 이동
- [x] StatisticsCards 컴포넌트
  - [x] 총 이벤트 수
  - [x] 총 선물 수
  - [x] 이번 달 이벤트 수
- [x] QuickActionButtons 컴포넌트
  - [x] 새 이벤트 추가 버튼
  - [x] 새 선물 추가 버튼
  - [x] AI 추천 요청 버튼
- [x] API 연동 (dashboardApi)

### 5. 이벤트 관리 페이지 (우선순위: 높음)
#### 5.1 이벤트 목록 페이지 (/events)
- [x] 페이지 레이아웃 구성
- [x] FilterTabs 컴포넌트 (전체, 다가오는 이벤트, 지난 이벤트)
- [x] SearchBar 컴포넌트 (이벤트명 검색)
- [x] EventCardGrid 컴포넌트
  - [x] EventCard 컴포넌트 구현
    - [x] 이벤트명
    - [x] D-Day 표시
    - [x] 날짜 표시
    - [x] 이벤트 타입 배지
    - [x] 클릭 시 상세 페이지 이동
- [x] Pagination 컴포넌트
- [x] CreateEventButton (플로팅 버튼)
- [x] API 연동 (eventsApi.getEvents)
- [x] 필터링 및 검색 기능
- [x] 로딩 상태 처리
- [x] 에러 처리

#### 5.2 이벤트 생성 페이지 (/events/new)
- [x] 페이지 레이아웃 구성
- [x] EventForm 컴포넌트 구현
  - [x] 이벤트명 입력 (필수)
  - [x] 날짜 선택 (date picker)
  - [x] 이벤트 타입 선택 (드롭다운)
  - [x] 대상자 입력 (선택)
  - [x] 관계 선택 (드롭다운)
  - [x] 메모 입력 (textarea)
  - [x] 추적 여부 토글
  - [x] 리마인더 설정 (다중 선택: 30일, 14일, 7일, 3일, 1일 전)
  - [x] 유효성 검증
- [x] 저장 버튼
- [x] 취소 버튼
- [x] API 연동 (eventsApi.createEvent)
- [x] 생성 성공 시 /events/:id 리다이렉트

#### 5.3 이벤트 상세 페이지 (/events/:id)
- [x] 페이지 레이아웃 구성
- [x] EventHeader 컴포넌트
  - [x] 이벤트명
  - [x] D-Day 카운터 (실시간)
- [x] EventInfoSection 컴포넌트
  - [x] 날짜, 타입, 대상자, 관계, 메모 표시
- [x] ReminderSection 컴포넌트
  - [x] 설정된 리마인더 목록
- [x] LinkedGiftsSection 컴포넌트
  - [x] 연결된 선물 목록
- [x] 수정 버튼
- [x] 삭제 버튼 (확인 다이얼로그)
- [x] API 연동 (eventsApi.getEventById)

#### 5.4 이벤트 수정 페이지 (/events/:id/edit)
- [x] 페이지 레이아웃 구성
- [x] EventForm 재사용 (기존 데이터 미리 채우기)
- [x] API 연동 (eventsApi.updateEvent)
- [x] 수정 성공 시 /events/:id 리다이렉트

### 6. 선물 관리 페이지 (우선순위: 높음)
#### 6.1 선물 목록 페이지 (/gifts)
- [x] 페이지 레이아웃 구성
- [x] FilterButtons 컴포넌트 (전체, 카테고리별, 구매 완료, 미구매)
- [x] SearchBar 컴포넌트 (선물명 검색)
- [x] GiftCardGrid 컴포넌트
  - [x] GiftCard 컴포넌트 구현
    - [x] 선물명
    - [x] 카테고리 배지
    - [x] 가격 표시
    - [x] 구매 여부 체크박스
    - [x] 클릭 시 상세 페이지 이동
- [x] Pagination 컴포넌트
- [x] AddGiftButton (플로팅 버튼)
- [x] API 연동 (giftsApi.getGifts)
- [x] 필터링 및 검색 기능
- [x] 구매 완료 토글 기능

#### 6.2 선물 추가 페이지 (/gifts/new)
- [x] 페이지 레이아웃 구성
- [x] GiftForm 컴포넌트 구현
  - [x] 선물명 입력 (필수)
  - [x] 카테고리 선택 (드롭다운)
  - [x] 가격 입력 (숫자)
  - [x] URL 입력
  - [x] 메모 입력 (textarea)
  - [x] 구매 여부 체크박스
  - [x] 연결 이벤트 선택 (드롭다운)
  - [x] 유효성 검증
- [x] 저장 버튼
- [x] 취소 버튼
- [x] API 연동 (giftsApi.createGift)
- [x] 생성 성공 시 /gifts 리다이렉트

#### 6.3 선물 상세 페이지 (/gifts/:id)
- [x] 페이지 레이아웃 구성
- [x] GiftHeader 컴포넌트
- [x] GiftInfoSection 컴포넌트
- [x] LinkedEventSection 컴포넌트
- [x] 구매 완료 토글 버튼
- [x] 수정 버튼
- [x] 삭제 버튼
- [x] API 연동 (giftsApi.getGiftById)

#### 6.4 선물 수정 페이지 (/gifts/:id/edit)
- [x] 페이지 레이아웃 구성
- [x] GiftForm 재사용 (기존 데이터 미리 채우기)
- [x] API 연동 (giftsApi.updateGift)
- [x] 수정 성공 시 /gifts/:id 리다이렉트

### 7. 공통 UI 컴포넌트 (shadcn/ui 기반)
#### 7.1 Atoms (기본 컴포넌트)
- [x] Button 컴포넌트 (Primary, Secondary, Ghost, Danger)
- [x] Input 컴포넌트
- [x] Label 컴포넌트
- [x] Badge 컴포넌트 (이벤트 타입, 카테고리)
- [x] Checkbox 컴포넌트
- [x] Select 컴포넌트 (드롭다운)
- [x] Textarea 컴포넌트
- [x] Switch 컴포넌트 (토글)
- [x] DatePicker 컴포넌트

#### 7.2 Molecules (조합 컴포넌트)
- [x] FormField 컴포넌트 (Label + Input + Error)
- [x] SearchBar 컴포넌트 (Input + Icon)
- [x] Card 컴포넌트 (기본 카드)
- [x] LoadingSpinner 컴포넌트
- [x] ErrorMessage 컴포넌트
- [x] EmptyState 컴포넌트

#### 7.3 Organisms (복잡한 컴포넌트)
- [x] Modal 컴포넌트 (확인 다이얼로그)
- [x] Toast 컴포넌트 (알림 메시지)
- [x] Pagination 컴포넌트
- [x] ConfirmDialog 컴포넌트 (삭제 확인 등)
- [x] Dropdown 컴포넌트 (프로필 드롭다운)

### 8. 상태 관리 및 API 통합
#### 8.1 Redux Slices
- [x] authSlice 구현
  - [x] token 상태
  - [x] user 상태
  - [x] login, logout 액션
- [x] eventsSlice 구현
  - [x] events 목록 상태
  - [x] filter 상태
  - [x] search 상태
- [x] giftsSlice 구현
  - [x] gifts 목록 상태
  - [x] filter 상태
  - [x] search 상태
- [x] uiSlice 구현
  - [x] sidebar 열림/닫힘 상태
  - [x] theme 상태 (다크 모드 준비)

#### 8.2 RTK Query API Services
- [x] authApi 구현
  - [x] login mutation
  - [x] signup mutation
  - [x] logout mutation
  - [x] passwordReset mutation
- [x] eventsApi 구현
  - [x] getEvents query
  - [x] getEventById query
  - [x] createEvent mutation
  - [x] updateEvent mutation
  - [x] deleteEvent mutation
- [x] giftsApi 구현
  - [x] getGifts query
  - [x] getGiftById query
  - [x] createGift mutation
  - [x] updateGift mutation
  - [x] deleteGift mutation
  - [x] togglePurchase mutation
- [x] dashboardApi 구현
  - [x] getDashboardData query (통합)

### 9. 라우팅 및 가드
- [x] 라우트 정의 (routes/index.tsx)
- [x] PrivateRoute 컴포넌트 (인증 가드)
- [x] PublicRoute 컴포넌트 (비인증 사용자만)
- [x] 404 NotFound 페이지
- [x] 리다이렉트 로직 구현

### 10. 유틸리티 및 헬퍼
- [x] dateUtils 구현
  - [x] D-Day 계산 함수
  - [x] 날짜 포맷팅 함수
- [x] validation 스키마 (Zod)
  - [x] 로그인 스키마
  - [x] 회원가입 스키마
  - [x] 이벤트 스키마
  - [x] 선물 스키마
- [x] constants 정의
  - [x] 이벤트 타입 목록
  - [x] 카테고리 목록
  - [x] 관계 목록

---

## 🚀 Phase 2: 확장 기능

### 11. AI 선물 추천 기능
#### 11.1 추천 요청 페이지 (/recommendations/new)
- [x] 페이지 레이아웃 구성
- [x] RecommendationForm 컴포넌트
  - [x] 이벤트 선택
  - [x] 예산 입력
  - [x] 선호 카테고리 선택 (다중)
  - [x] 추가 메시지 입력
- [x] API 연동 (recommendationsApi.createRequest)
- [x] 로딩 상태 표시 (AI 처리 중)
- [x] 추천 완료 시 /recommendations/:id 리다이렉트

#### 11.2 추천 결과 페이지 (/recommendations/:id)
- [x] 페이지 레이아웃 구성
- [x] RequestInfoSection 컴포넌트
- [x] UserSavedGiftsSection 컴포넌트 (우선 표시)
- [x] AIRecommendationsSection 컴포넌트
  - [x] 추천 선물 카드
  - [x] 추천 이유 표시
  - [x] 선물 리스트에 저장 버튼
- [x] API 연동 (recommendationsApi.getRecommendationById)

#### 11.3 추천 내역 페이지 (/recommendations)
- [x] 페이지 레이아웃 구성
- [x] RecommendationCardList 컴포넌트
- [x] 새 추천 요청 버튼
- [x] API 연동 (recommendationsApi.getRecommendations)

### 12. 리마인더 페이지
#### 12.1 리마인더 설정 페이지 (/reminders)
- [x] 페이지 레이아웃 구성
- [x] ReminderCardList 컴포넌트
- [x] GlobalReminderSettings 컴포넌트
- [x] API 연동 (remindersApi)

#### 12.2 리마인더 발송 내역 페이지 (/reminders/logs)
- [x] 페이지 레이아웃 구성
- [x] FilterButtons 컴포넌트 (전체, 성공, 실패)
- [x] ReminderLogTable 컴포넌트
- [x] 재시도 버튼
- [x] Pagination 컴포넌트
- [x] API 연동 (remindersApi.getLogs)

### 13. 캘린더 뷰 페이지 (/calendar)
- [x] 페이지 레이아웃 구성
- [x] CalendarView 컴포넌트 (react-big-calendar)
- [x] 월별 이벤트 마커 표시
- [x] EventPopup 컴포넌트 (날짜 클릭 시)
- [x] 월 이동 버튼
- [x] API 연동 (eventsApi.getEventsByMonth)

### 14. 설정 페이지
#### 14.1 프로필 설정 (/settings/profile)
- [x] 페이지 레이아웃 구성
- [x] SettingsTabs 컴포넌트 (프로필, 알림, 계정)
- [x] ProfileForm 컴포넌트
  - [x] 프로필 사진 업로드
  - [x] 닉네임 수정
  - [x] 이메일 표시 (읽기 전용)
- [x] 저장 버튼
- [x] API 연동 (usersApi.updateProfile)

#### 14.2 알림 설정 (/settings/notifications)
- [x] 페이지 레이아웃 구성
- [x] NotificationSettings 컴포넌트
  - [x] 이메일 알림 on/off 토글
  - [x] 리마인더 발송 시간 선택
- [x] 저장 버튼
- [x] API 연동 (usersApi.updateNotificationSettings)

#### 14.3 계정 관리 (/settings/account)
- [x] 페이지 레이아웃 구성
- [x] PasswordChangeForm 컴포넌트
  - [x] 현재 비밀번호 입력
  - [x] 새 비밀번호 입력
  - [x] 새 비밀번호 확인
- [x] 로그아웃 버튼
- [x] 계정 삭제 버튼 (확인 다이얼로그)
- [x] API 연동 (usersApi.changePassword, usersApi.deleteAccount)

### 15. 랜딩 페이지 (/) - 공개 홈
- [x] 페이지 레이아웃 구성
- [x] HeroSection 컴포넌트
  - [x] 메인 타이틀
  - [x] 서브 타이틀
  - [x] CTA 버튼 (로그인/회원가입)
- [x] FeaturesSection 컴포넌트
  - [x] 주요 기능 3가지 소개
- [x] Footer 컴포넌트

---

## 🎨 Phase 3: 고도화

### 16. 성능 최적화
- [x] React.lazy를 활용한 코드 스플리팅
- [x] React.memo를 활용한 컴포넌트 메모이제이션
- [x] 이미지 최적화 (WebP, lazy loading) - 가이드 문서화
- [x] Virtual scrolling (react-window) 적용 - 가이드 문서화

### 17. 테스트 작성
- [x] Vitest 설정
- [x] React Testing Library 설정
- [x] 유틸리티 함수 단위 테스트
- [x] 컴포넌트 렌더링 테스트
- [x] API 통합 테스트 (MSW)

### 18. 다크 모드
- [x] 다크 모드 테마 정의
- [x] 테마 토글 버튼 구현
- [x] 로컬스토리지에 테마 저장
- [x] 전역 테마 적용

### 19. 접근성 개선
- [ ] ARIA 속성 추가
- [ ] 키보드 네비게이션 개선
- [ ] 포커스 관리
- [ ] 색상 대비 검증

### 20. PWA 지원
- [ ] manifest.json 작성
- [ ] Service Worker 등록
- [ ] 오프라인 지원
- [ ] 푸시 알림 (선택)

---

## 📁 예상 폴더 구조

```
frontend/
├── public/
│   ├── favicon.ico
│   └── robots.txt
│
├── src/
│   ├── assets/
│   │   ├── images/
│   │   └── fonts/
│   │
│   ├── components/
│   │   ├── ui/                    # shadcn/ui 컴포넌트
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── card.tsx
│   │   │   ├── select.tsx
│   │   │   ├── checkbox.tsx
│   │   │   ├── switch.tsx
│   │   │   ├── textarea.tsx
│   │   │   ├── badge.tsx
│   │   │   └── date-picker.tsx
│   │   │
│   │   ├── layout/
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── PageLayout.tsx
│   │   │   └── AuthLayout.tsx
│   │   │
│   │   ├── features/
│   │   │   ├── events/
│   │   │   │   ├── EventCard.tsx
│   │   │   │   ├── EventForm.tsx
│   │   │   │   ├── EventList.tsx
│   │   │   │   ├── EventHeader.tsx
│   │   │   │   ├── EventInfoSection.tsx
│   │   │   │   ├── ReminderSection.tsx
│   │   │   │   └── LinkedGiftsSection.tsx
│   │   │   │
│   │   │   ├── gifts/
│   │   │   │   ├── GiftCard.tsx
│   │   │   │   ├── GiftForm.tsx
│   │   │   │   ├── GiftList.tsx
│   │   │   │   ├── GiftHeader.tsx
│   │   │   │   └── GiftInfoSection.tsx
│   │   │   │
│   │   │   ├── recommendations/
│   │   │   │   ├── RecommendationForm.tsx
│   │   │   │   ├── RecommendationCard.tsx
│   │   │   │   ├── RequestInfoSection.tsx
│   │   │   │   ├── UserSavedGiftsSection.tsx
│   │   │   │   └── AIRecommendationsSection.tsx
│   │   │   │
│   │   │   ├── dashboard/
│   │   │   │   ├── WelcomeBanner.tsx
│   │   │   │   ├── UpcomingEventsWidget.tsx
│   │   │   │   ├── StatisticsCards.tsx
│   │   │   │   └── QuickActionButtons.tsx
│   │   │   │
│   │   │   ├── auth/
│   │   │   │   ├── LoginForm.tsx
│   │   │   │   ├── SignupForm.tsx
│   │   │   │   └── PasswordResetForm.tsx
│   │   │   │
│   │   │   ├── reminders/
│   │   │   │   ├── ReminderCard.tsx
│   │   │   │   ├── ReminderLogTable.tsx
│   │   │   │   └── GlobalReminderSettings.tsx
│   │   │   │
│   │   │   ├── calendar/
│   │   │   │   ├── CalendarView.tsx
│   │   │   │   └── EventPopup.tsx
│   │   │   │
│   │   │   └── settings/
│   │   │       ├── ProfileForm.tsx
│   │   │       ├── NotificationSettings.tsx
│   │   │       ├── PasswordChangeForm.tsx
│   │   │       └── SettingsTabs.tsx
│   │   │
│   │   └── common/
│   │       ├── SearchBar.tsx
│   │       ├── FilterButtons.tsx
│   │       ├── FilterTabs.tsx
│   │       ├── Pagination.tsx
│   │       ├── LoadingSpinner.tsx
│   │       ├── ErrorMessage.tsx
│   │       ├── EmptyState.tsx
│   │       ├── ConfirmDialog.tsx
│   │       ├── Toast.tsx
│   │       └── ErrorBoundary.tsx
│   │
│   ├── pages/
│   │   ├── Home.tsx
│   │   ├── Login.tsx
│   │   ├── Signup.tsx
│   │   ├── PasswordReset.tsx
│   │   ├── Dashboard.tsx
│   │   ├── EventListPage.tsx
│   │   ├── EventDetailPage.tsx
│   │   ├── EventCreatePage.tsx
│   │   ├── EventEditPage.tsx
│   │   ├── GiftListPage.tsx
│   │   ├── GiftDetailPage.tsx
│   │   ├── GiftCreatePage.tsx
│   │   ├── GiftEditPage.tsx
│   │   ├── RecommendationListPage.tsx
│   │   ├── RecommendationRequestPage.tsx
│   │   ├── RecommendationDetailPage.tsx
│   │   ├── ReminderListPage.tsx
│   │   ├── ReminderLogsPage.tsx
│   │   ├── CalendarPage.tsx
│   │   ├── SettingsProfilePage.tsx
│   │   ├── SettingsNotificationsPage.tsx
│   │   ├── SettingsAccountPage.tsx
│   │   └── NotFoundPage.tsx
│   │
│   ├── store/
│   │   ├── index.ts               # Store 설정
│   │   ├── slices/
│   │   │   ├── authSlice.ts
│   │   │   ├── eventsSlice.ts
│   │   │   ├── giftsSlice.ts
│   │   │   └── uiSlice.ts
│   │   │
│   │   └── services/              # RTK Query API
│   │       ├── authApi.ts
│   │       ├── eventsApi.ts
│   │       ├── giftsApi.ts
│   │       ├── recommendationsApi.ts
│   │       ├── remindersApi.ts
│   │       ├── dashboardApi.ts
│   │       └── usersApi.ts
│   │
│   ├── routes/
│   │   ├── index.tsx              # 라우트 정의
│   │   ├── PrivateRoute.tsx       # 인증 가드
│   │   └── PublicRoute.tsx        # 공개 라우트
│   │
│   ├── hooks/
│   │   ├── useAuth.ts
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   └── usePagination.ts
│   │
│   ├── utils/
│   │   ├── dateUtils.ts           # D-Day 계산, 포맷팅
│   │   ├── validation.ts          # Zod 스키마
│   │   └── constants.ts           # 상수 정의
│   │
│   ├── types/
│   │   ├── event.ts
│   │   ├── gift.ts
│   │   ├── user.ts
│   │   ├── recommendation.ts
│   │   ├── reminder.ts
│   │   └── index.ts
│   │
│   ├── styles/
│   │   ├── globals.css
│   │   └── variables.css
│   │
│   ├── lib/
│   │   └── utils.ts               # shadcn/ui 유틸
│   │
│   ├── App.tsx
│   ├── main.tsx
│   └── vite-env.d.ts
│
├── .env.example
├── .gitignore
├── package.json
├── tsconfig.json
├── vite.config.ts
├── tailwind.config.js
├── postcss.config.js
├── components.json
└── README.md
```

---

## 📌 우선순위 정리

### 최우선 (Sprint 1-2)
1. 프로젝트 초기 설정
2. 공통 레이아웃 (Header, Sidebar, Footer)
3. 인증 페이지 (로그인, 회원가입)
4. 대시보드 페이지
5. 이벤트 목록/생성/상세 페이지
6. 선물 목록/추가/상세 페이지

### 중요 (Sprint 3-4)
1. 이벤트 수정/삭제 기능
2. 선물 수정/삭제 기능
3. 설정 페이지 (프로필, 알림, 계정)
4. 공통 UI 컴포넌트 완성

### 추가 기능 (Sprint 5-6)
1. AI 추천 기능
2. 리마인더 페이지
3. 캘린더 뷰
4. 랜딩 페이지

### 고도화 (Sprint 7+)
1. 성능 최적화
2. 테스트 작성
3. 다크 모드
4. 접근성 개선
5. PWA 지원

---

## 🔗 참고 문서

- [IA 문서](./ia_structure.md)
- [페이지 정의서](./page_definition.md)
- [기능 상세 명세서](./functional_spec.md)
- [UI/UX 가이드](./uiux_guide.md)
- [기술 요약](./tech_summary.md)
- [프로젝트 아이디어](./project_idea.md)

---

**작성일**: 2025-01-11
**버전**: 1.0
**전체 항목 수**: 80개
**완료 항목 수**: 0개
**진행률**: 0%
