# Day Memory Frontend

React + TypeScript + Redux 기반 기념일 관리 프론트엔드

## 기술 스택

- React 18
- TypeScript
- Redux Toolkit
- React Router DOM
- Axios

## 설치

```bash
npm install
```

## 실행

```bash
npm start
```

개발 서버는 http://localhost:3000에서 실행됩니다.

## 빌드

```bash
npm run build
```

## 주요 페이지

- **Dashboard** (`/`) - 다가오는 이벤트 (30일 이내) 대시보드
- **Events** (`/events`) - 모든 이벤트 관리
- **Gifts** (`/gifts`) - 선물 아이디어 관리

## Redux Store 구조

### Event Slice
- `events`: 사용자의 모든 이벤트
- `upcomingEvents`: 다가오는 이벤트
- `currentEvent`: 현재 선택된 이벤트
- Actions:
  - `fetchEvents` - 이벤트 목록 조회
  - `fetchUpcomingEvents` - 다가오는 이벤트 조회
  - `createEvent` - 새 이벤트 생성
  - `updateEvent` - 이벤트 수정
  - `deleteEvent` - 이벤트 삭제

### Gift Slice
- `gifts`: 사용자의 모든 선물 아이템
- `eventGifts`: 특정 이벤트의 선물 아이템
- Actions:
  - `fetchGiftItems` - 선물 목록 조회
  - `fetchGiftItemsByEvent` - 이벤트별 선물 조회
  - `createGiftItem` - 새 선물 생성
  - `updateGiftItem` - 선물 수정
  - `togglePurchaseStatus` - 구매 상태 토글
  - `deleteGiftItem` - 선물 삭제

## API 통합

백엔드 API 베이스 URL은 [api/axios.ts](src/api/axios.ts)에서 설정됩니다:

```typescript
baseURL: 'http://localhost:8080/api'
```

## 프로젝트 구조

```
src/
├── api/                # API 호출 함수
├── components/         # 재사용 가능한 컴포넌트
├── pages/              # 페이지 컴포넌트
├── store/              # Redux store 및 slices
├── types/              # TypeScript 타입 정의
└── utils/              # 유틸리티 함수
```

## TODO

- [ ] 사용자 인증 시스템 추가
- [ ] 이벤트 생성/수정 폼 컴포넌트 추가
- [ ] 선물 생성/수정 폼 컴포넌트 추가
- [ ] AI 기반 선물 추천 기능 통합
- [ ] 푸시 알림 기능 추가
- [ ] 반응형 디자인 개선
- [ ] 다크 모드 지원
