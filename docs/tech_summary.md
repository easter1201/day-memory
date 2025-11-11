# 기술 요약 문서 (Technical Summary) - Day Memory Frontend

## 📋 개요
Day Memory 프론트엔드 프로젝트의 기술 스택, 아키텍처, 개발 환경 및 주요 규칙을 요약한 문서입니다. 실무 개발자들이 프로젝트 전반을 한눈에 이해할 수 있도록 작성되었습니다.

---

## 🛠️ 사용 기술 스택

### Core Technologies
- **Framework**: React 18+
- **Build Tool**: Vite 5+
- **Language**: TypeScript 5+
- **Styling**: TailwindCSS 3
- **UI Components**: shadcn/ui (Radix UI 기반)

### State Management & Data Fetching
- **State Management**: Redux Toolkit + RTK Query
- **Form Management**: React Hook Form
- **Data Validation**: Zod

### Routing & Navigation
- **Routing**: React Router v6

### Additional Libraries
- **Date Handling**: date-fns
- **HTTP Client**: Axios (RTK Query 내부)
- **Icons**: Heroicons 또는 Lucide React
- **Calendar**: React Big Calendar (예정)

---

## 🏗️ 주요 아키텍처 개요

### 컴포넌트 구조
```
Atomic Design Pattern 기반 계층 구조

1. Atoms (원자)
   - Button, Input, Label, Badge 등 기본 UI 요소

2. Molecules (분자)
   - FormField (Label + Input + Error)
   - SearchBar (Input + Button)
   - Card 컴포넌트

3. Organisms (유기체)
   - EventCard, GiftCard
   - Navigation (Header, Sidebar)
   - Modal, Toast

4. Templates (템플릿)
   - PageLayout (공통 레이아웃)
   - AuthLayout (인증 페이지 레이아웃)

5. Pages (페이지)
   - Dashboard, EventList, GiftList 등
```

### 상태 관리 전략
```typescript
// Redux Toolkit 슬라이스 구조
- authSlice: 사용자 인증 상태 (token, user info)
- eventsSlice: 이벤트 목록 및 필터 상태
- giftsSlice: 선물 목록 및 필터 상태
- uiSlice: UI 상태 (sidebar 열림/닫힘, 테마 등)

// RTK Query API 서비스
- authApi: 로그인, 회원가입, 로그아웃
- eventsApi: 이벤트 CRUD
- giftsApi: 선물 CRUD
- recommendationsApi: AI 추천
- remindersApi: 리마인더 관리
```

### 라우팅 구조
```typescript
// Public Routes
- / (Home)
- /login
- /signup
- /password-reset

// Protected Routes (인증 필요)
- /dashboard
- /events, /events/:id, /events/new, /events/:id/edit
- /gifts, /gifts/:id, /gifts/new, /gifts/:id/edit
- /recommendations, /recommendations/new, /recommendations/:id
- /reminders, /reminders/logs
- /calendar
- /settings/profile, /settings/notifications, /settings/account

// Route Guards
- PrivateRoute: 인증된 사용자만 접근
- PublicRoute: 비인증 사용자만 접근 (로그인 시 /dashboard 리다이렉트)
```

---

## 🔧 개발 환경 및 빌드 도구

### 개발 환경 설정
```bash
# 프로젝트 초기화
npm create vite@latest frontend -- --template react-ts

# 의존성 설치
npm install react-router-dom @reduxjs/toolkit react-redux
npm install react-hook-form zod @hookform/resolvers
npm install axios date-fns
npm install -D tailwindcss postcss autoprefixer
npm install -D @types/node

# shadcn/ui 초기화
npx shadcn-ui@latest init
```

### 빌드 도구 (Vite)
```typescript
// vite.config.ts
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

### 개발 스크립트
```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "format": "prettier --write \"src/**/*.{ts,tsx,css}\""
  }
}
```

---

## 📊 데이터 흐름 및 API 통합

### API 통신 구조
```
Component
    ↓ dispatch action
RTK Query Hook (useGetEventsQuery)
    ↓ HTTP Request
Backend API (/api/events)
    ↓ Response
RTK Query Cache
    ↓ auto re-render
Component (updated data)
```

### RTK Query 사용 예시
```typescript
// services/eventsApi.ts
export const eventsApi = createApi({
  reducerPath: 'eventsApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as RootState).auth.token;
      if (token) {
        headers.set('Authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  endpoints: (builder) => ({
    getEvents: builder.query<Event[], void>({
      query: () => '/events',
    }),
    createEvent: builder.mutation<Event, Partial<Event>>({
      query: (newEvent) => ({
        url: '/events',
        method: 'POST',
        body: newEvent,
      }),
    }),
  }),
});
```

### 에러 핸들링
```typescript
// API 에러 처리
- 401 Unauthorized → 자동 로그아웃 + /login 리다이렉트
- 400 Bad Request → 폼 검증 에러 메시지 표시
- 404 Not Found → 404 페이지 표시
- 500 Server Error → 에러 토스트 메시지

// Axios Interceptor 설정
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      store.dispatch(logout());
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 🎨 퍼블리싱 관련 주요 규칙

### 반응형 디자인
```css
/* Breakpoints */
Mobile: < 640px (1단 레이아웃)
Tablet: 640px ~ 1024px (2단 레이아웃)
Desktop: > 1024px (3~4단 레이아웃)
Wide: > 1280px

/* Tailwind CSS 사용 예시 */
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
  {/* 카드 목록 */}
</div>
```

### 디자인 시스템
```typescript
// Tailwind Config (tailwind.config.js)
export default {
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#EFF6FF',
          500: '#3B82F6',
          600: '#2563EB', // 기본값
          700: '#1D4ED8',
        },
        secondary: {
          500: '#A855F7',
          600: '#9333EA', // 기본값
        },
      },
      fontFamily: {
        sans: ['Pretendard', 'Inter', 'sans-serif'],
      },
    },
  },
};

// shadcn/ui 컴포넌트 사용
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card } from '@/components/ui/card';
```

### 접근성 (Accessibility)
```typescript
// ARIA 속성 사용
<button aria-label="이벤트 삭제" onClick={handleDelete}>
  <TrashIcon />
</button>

// 키보드 네비게이션
- Tab 순서 논리적 구성
- Enter/Space로 버튼 활성화
- Esc로 모달 닫기

// 색상 대비
- WCAG 2.1 AA 기준 (4.5:1 대비율)
- 주요 텍스트와 배경 간 충분한 대비
```

### 스타일링 규칙
```typescript
// 1. Tailwind CSS 우선 사용
<button className="px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700">
  버튼
</button>

// 2. 복잡한 스타일은 CSS 모듈 또는 shadcn/ui 컴포넌트
// 3. 인라인 스타일은 동적 값에만 사용
<div style={{ width: `${progress}%` }} />

// 4. 일관된 간격 사용 (Tailwind spacing scale)
gap-4, p-6, mt-8 등
```

---

## 📁 예상 폴더 구조

```
frontend/
├── public/
│   ├── favicon.ico
│   └── robots.txt
├── src/
│   ├── assets/                    # 이미지, 폰트 등
│   │   ├── images/
│   │   └── fonts/
│   │
│   ├── components/                # UI 컴포넌트
│   │   ├── ui/                    # shadcn/ui 컴포넌트
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── card.tsx
│   │   │   └── ...
│   │   ├── layout/                # 레이아웃 컴포넌트
│   │   │   ├── Header.tsx
│   │   │   ├── Sidebar.tsx
│   │   │   ├── Footer.tsx
│   │   │   └── PageLayout.tsx
│   │   ├── features/              # 기능별 컴포넌트
│   │   │   ├── events/
│   │   │   │   ├── EventCard.tsx
│   │   │   │   ├── EventForm.tsx
│   │   │   │   └── EventList.tsx
│   │   │   ├── gifts/
│   │   │   │   ├── GiftCard.tsx
│   │   │   │   ├── GiftForm.tsx
│   │   │   │   └── GiftList.tsx
│   │   │   └── recommendations/
│   │   │       ├── RecommendationRequest.tsx
│   │   │       └── RecommendationResult.tsx
│   │   └── common/                # 공통 컴포넌트
│   │       ├── SearchBar.tsx
│   │       ├── Pagination.tsx
│   │       ├── LoadingSpinner.tsx
│   │       └── ErrorBoundary.tsx
│   │
│   ├── pages/                     # 페이지 컴포넌트
│   │   ├── Home.tsx
│   │   ├── Login.tsx
│   │   ├── Signup.tsx
│   │   ├── Dashboard.tsx
│   │   ├── EventListPage.tsx
│   │   ├── EventDetailPage.tsx
│   │   ├── GiftListPage.tsx
│   │   └── ...
│   │
│   ├── store/                     # Redux 상태 관리
│   │   ├── index.ts               # Store 설정
│   │   ├── slices/
│   │   │   ├── authSlice.ts
│   │   │   ├── eventsSlice.ts
│   │   │   ├── giftsSlice.ts
│   │   │   └── uiSlice.ts
│   │   └── services/              # RTK Query API
│   │       ├── authApi.ts
│   │       ├── eventsApi.ts
│   │       ├── giftsApi.ts
│   │       └── recommendationsApi.ts
│   │
│   ├── routes/                    # 라우팅 설정
│   │   ├── index.tsx              # 라우트 정의
│   │   ├── PrivateRoute.tsx       # 인증 가드
│   │   └── PublicRoute.tsx        # 공개 라우트
│   │
│   ├── hooks/                     # 커스텀 훅
│   │   ├── useAuth.ts
│   │   ├── useDebounce.ts
│   │   ├── useLocalStorage.ts
│   │   └── usePagination.ts
│   │
│   ├── utils/                     # 유틸리티 함수
│   │   ├── dateUtils.ts           # 날짜 포맷팅, D-Day 계산
│   │   ├── validation.ts          # 검증 함수
│   │   └── constants.ts           # 상수 정의
│   │
│   ├── types/                     # TypeScript 타입 정의
│   │   ├── event.ts
│   │   ├── gift.ts
│   │   ├── user.ts
│   │   └── index.ts
│   │
│   ├── styles/                    # 전역 스타일
│   │   ├── globals.css            # Tailwind directives
│   │   └── variables.css          # CSS 변수
│   │
│   ├── lib/                       # 설정 파일
│   │   └── utils.ts               # shadcn/ui 유틸
│   │
│   ├── App.tsx                    # 메인 앱 컴포넌트
│   ├── main.tsx                   # 진입점
│   └── vite-env.d.ts              # Vite 타입 정의
│
├── .env.example                   # 환경 변수 예시
├── .gitignore
├── package.json
├── tsconfig.json                  # TypeScript 설정
├── vite.config.ts                 # Vite 설정
├── tailwind.config.js             # Tailwind 설정
├── postcss.config.js              # PostCSS 설정
├── components.json                # shadcn/ui 설정
└── README.md
```

---

## 🚀 향후 확장 및 유지보수 고려사항

### 코드 품질 유지
```typescript
// ESLint + Prettier 설정
- ESLint: 코드 품질 검사 (React 규칙, TypeScript 규칙)
- Prettier: 코드 포맷팅 자동화
- Husky + lint-staged: 커밋 전 자동 검사

// TypeScript Strict Mode 활성화
{
  "compilerOptions": {
    "strict": true,
    "noUncheckedIndexedAccess": true,
    "noImplicitOverride": true
  }
}
```

### 성능 최적화
```typescript
// 1. Code Splitting (React.lazy)
const EventListPage = React.lazy(() => import('./pages/EventListPage'));

// 2. Memoization
const MemoizedEventCard = React.memo(EventCard);

// 3. Virtual Scrolling (react-window)
import { FixedSizeList } from 'react-window';

// 4. Image Optimization
- WebP 포맷 사용
- Lazy loading (loading="lazy")
- 적절한 이미지 크기 사용
```

### 테스트 전략
```typescript
// 1. Unit Tests (Vitest)
- 유틸리티 함수 테스트
- 커스텀 훅 테스트

// 2. Component Tests (React Testing Library)
- UI 컴포넌트 렌더링 테스트
- 사용자 인터랙션 테스트

// 3. E2E Tests (Playwright 또는 Cypress)
- 주요 사용자 플로우 테스트
- 회원가입 → 로그인 → 이벤트 생성 플로우
```

### 보안 고려사항
```typescript
// 1. XSS 방지
- React의 자동 이스케이핑 활용
- dangerouslySetInnerHTML 사용 최소화

// 2. CSRF 방지
- JWT 토큰을 Authorization 헤더에 포함
- HttpOnly 쿠키 사용 (선택)

// 3. 민감 정보 보호
- .env 파일에 API 키 저장
- .gitignore에 .env 추가
- 프론트엔드에 민감 정보 노출 금지

// 4. Dependency 보안
- npm audit으로 정기적 점검
- Dependabot 활용
```

### 모니터링 및 로깅
```typescript
// 1. Error Boundary
class ErrorBoundary extends React.Component {
  componentDidCatch(error, errorInfo) {
    // 에러 로깅 서비스로 전송 (Sentry 등)
    console.error('Error:', error, errorInfo);
  }
}

// 2. Analytics (선택)
- Google Analytics
- Mixpanel
- Amplitude

// 3. Performance Monitoring
- Web Vitals 측정 (LCP, FID, CLS)
- Lighthouse CI 통합
```

### CI/CD 파이프라인
```yaml
# .github/workflows/frontend-ci.yml
name: Frontend CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
      - name: Install dependencies
        run: npm ci
      - name: Lint
        run: npm run lint
      - name: Type check
        run: npm run type-check
      - name: Build
        run: npm run build
      - name: Upload build artifacts
        uses: actions/upload-artifact@v4
        with:
          name: dist
          path: dist/
```

### 배포 전략
```typescript
// 1. 정적 호스팅 (Vercel, Netlify, GitHub Pages)
- Vite 빌드 결과물(dist/) 배포
- 자동 배포 설정

// 2. Docker 컨테이너화
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

// 3. 환경별 설정
- .env.development (개발 환경)
- .env.production (프로덕션 환경)
```

### 문서화
```typescript
// 1. 컴포넌트 문서화 (Storybook 선택)
- UI 컴포넌트 카탈로그
- 인터랙티브 문서

// 2. API 문서화
- Backend API 문서 참조
- RTK Query 엔드포인트 주석

// 3. README.md 작성
- 프로젝트 소개
- 설치 및 실행 방법
- 개발 가이드
- 기여 가이드
```

---

## 🔗 참고 문서

- [IA 문서 (Information Architecture)](./ia_structure.md)
- [페이지 정의서 (Page Definition)](./page_definition.md)
- [기능 상세 명세서 (Functional Spec)](./functional_spec.md)
- [UI/UX 상세 가이드](./uiux_guide.md)
- [프로젝트 아이디어](./project_idea.md)

---

**작성일**: 2025-01-11
**버전**: 1.0
**기반 기술**: React 18 + Vite 5 + TypeScript 5 + TailwindCSS 3 + shadcn/ui
