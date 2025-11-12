# 성능 최적화 가이드

## 구현된 최적화

### 1. 코드 스플리팅 (Code Splitting)

React.lazy()를 사용하여 모든 페이지 컴포넌트를 지연 로딩합니다.

**구현 위치**: `frontend/src/routes/index.tsx`

```typescript
// 페이지 컴포넌트를 lazy로 import
const DashboardPage = lazy(() => import("../pages/DashboardPage").then(m => ({ default: m.DashboardPage })));
const EventsListPage = lazy(() => import("../pages/EventsListPage").then(m => ({ default: m.EventsListPage })));
// ... 기타 페이지들

// Suspense로 감싸서 로딩 fallback 제공
const SuspenseWrapper = ({ children }) => (
  <Suspense fallback={<PageLoader />}>{children}</Suspense>
);
```

**효과**:
- 초기 번들 크기 감소
- 페이지별 필요한 코드만 로드
- 첫 로딩 시간 단축

### 2. 컴포넌트 메모이제이션

자주 재렌더링되는 UI 컴포넌트에 React.memo 적용:

- `Badge` 컴포넌트
- `LoadingSpinner` 컴포넌트

**사용 예시**:
```typescript
export const LoadingSpinner = React.memo<LoadingSpinnerProps>(({ size, className, text }) => {
  // 컴포넌트 로직
});

LoadingSpinner.displayName = "LoadingSpinner";
```

**효과**:
- 불필요한 재렌더링 방지
- props가 변경되지 않으면 리렌더링 스킵
- 전체 앱 성능 향상

## 추가 최적화 가이드

### 3. 이미지 최적화

#### WebP 포맷 사용
```jsx
<picture>
  <source srcSet="/image.webp" type="image/webp" />
  <img src="/image.jpg" alt="설명" />
</picture>
```

#### 이미지 Lazy Loading
```jsx
<img src="/image.jpg" alt="설명" loading="lazy" />
```

#### 반응형 이미지
```jsx
<img
  srcSet="/image-small.jpg 400w, /image-medium.jpg 800w, /image-large.jpg 1200w"
  sizes="(max-width: 600px) 400px, (max-width: 900px) 800px, 1200px"
  src="/image-medium.jpg"
  alt="설명"
/>
```

### 4. Virtual Scrolling (react-window)

긴 목록 렌더링 최적화:

#### 설치
```bash
npm install react-window
npm install --save-dev @types/react-window
```

#### 사용 예시
```typescript
import { FixedSizeList } from 'react-window';

function EventsList({ events }) {
  const Row = ({ index, style }) => (
    <div style={style}>
      <EventCard event={events[index]} />
    </div>
  );

  return (
    <FixedSizeList
      height={600}
      itemCount={events.length}
      itemSize={100}
      width="100%"
    >
      {Row}
    </FixedSizeList>
  );
}
```

**적용 권장 대상**:
- 이벤트 목록 (EventsListPage)
- 선물 목록 (GiftListPage)
- 추천 목록 (RecommendationsListPage)

## 성능 측정

### Chrome DevTools
1. Lighthouse 실행 (Performance 측정)
2. Performance 탭에서 프로파일링
3. Network 탭에서 번들 크기 확인

### React DevTools Profiler
```jsx
import { Profiler } from 'react';

<Profiler id="EventsList" onRender={onRenderCallback}>
  <EventsList />
</Profiler>
```

## 베스트 프랙티스

1. **useMemo와 useCallback 활용**
   ```typescript
   const expensiveValue = useMemo(() => computeExpensiveValue(a, b), [a, b]);
   const memoizedCallback = useCallback(() => doSomething(a, b), [a, b]);
   ```

2. **조건부 렌더링 최적화**
   ```typescript
   // Bad
   {showModal && <Modal {...props} />}

   // Good (Modal 내부에서 조건 처리)
   <Modal isOpen={showModal} {...props} />
   ```

3. **상태 업데이트 배칭**
   ```typescript
   // React 18의 자동 배칭 활용
   setState1(newValue1);
   setState2(newValue2); // 자동으로 배칭됨
   ```

4. **데이터 페칭 최적화**
   - RTK Query의 캐싱 활용
   - Stale-while-revalidate 패턴
   - 필요한 데이터만 요청

## 번들 크기 최적화

### Tree Shaking 확인
```bash
npm run build
npm run analyze # webpack-bundle-analyzer 필요
```

### 중복 제거
- lodash 대신 lodash-es 사용
- date-fns에서 필요한 함수만 import
- 사용하지 않는 의존성 제거

## 모니터링

### 프로덕션 모니터링 도구
- Sentry (에러 트래킹)
- Google Analytics (사용자 행동)
- Web Vitals (Core Web Vitals 측정)

```typescript
import { getCLS, getFID, getFCP, getLCP, getTTFB } from 'web-vitals';

getCLS(console.log);
getFID(console.log);
getFCP(console.log);
getLCP(console.log);
getTTFB(console.log);
```
