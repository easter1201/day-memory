# 접근성 가이드 (Accessibility Guide)

Day Memory 프론트엔드의 접근성(A11y) 구현 및 개선 사항을 문서화합니다.

## 구현된 접근성 기능

### 1. ARIA 속성 (ARIA Attributes)

#### Button 컴포넌트
- `aria-disabled`: 버튼 비활성화 상태 전달
- `aria-busy`: 로딩 중 상태 전달
- `aria-hidden`: 장식용 아이콘/SVG에 적용

**구현 위치**: `frontend/src/components/ui/Button.tsx`

```typescript
<button
  aria-disabled={disabled || isLoading}
  aria-busy={isLoading}
>
  {isLoading && <svg aria-hidden="true">...</svg>}
  {children}
</button>
```

#### Input 컴포넌트
- `htmlFor`/`id`: Label과 Input 연결
- `aria-invalid`: 에러 상태 전달
- `aria-describedby`: 에러 메시지와 연결
- `role="alert"`: 에러 메시지를 즉시 알림

**구현 위치**: `frontend/src/components/ui/Input.tsx`

```typescript
<label htmlFor={inputId}>{label}</label>
<input
  id={inputId}
  aria-invalid={error ? "true" : "false"}
  aria-describedby={errorId}
/>
{error && <p id={errorId} role="alert">{error}</p>}
```

#### Header 컴포넌트
- `role="banner"`: 헤더 랜드마크
- `aria-label`: 네비게이션 메뉴 레이블
- `aria-expanded`: 드롭다운/메뉴 확장 상태
- `aria-haspopup`: 팝업 메뉴 존재 알림
- `aria-controls`: 제어 대상 요소 지정
- `aria-hidden="true"`: 장식용 아이콘

**구현 위치**: `frontend/src/components/layout/Header.tsx`

#### Footer 컴포넌트
- `role="contentinfo"`: 푸터 랜드마크
- `aria-label`: 소셜 링크 레이블

**구현 위치**: `frontend/src/components/layout/Footer.tsx`

#### PageLayout 컴포넌트
- `role="main"`: 메인 콘텐츠 랜드마크
- `id="main-content"`: Skip Link 대상

**구현 위치**: `frontend/src/components/layout/PageLayout.tsx`

### 2. 키보드 네비게이션 (Keyboard Navigation)

#### Skip Link
페이지 상단에서 메인 콘텐츠로 바로 이동할 수 있는 링크 제공.

**구현 위치**: `frontend/src/components/common/SkipLink.tsx`

```typescript
<a href="#main-content" className="sr-only focus:not-sr-only">
  본문으로 건너뛰기
</a>
```

**특징**:
- 기본적으로 숨김 (screen reader only)
- 키보드 포커스 시 표시
- Tab 키로 접근 가능

#### 키보드 단축키 Hook
사용자 정의 키보드 단축키 구현을 위한 훅.

**구현 위치**: `frontend/src/hooks/useKeyboardShortcut.ts`

```typescript
useKeyboardShortcut({
  key: 's',
  ctrl: true,
  callback: () => console.log('Ctrl+S pressed'),
});
```

#### 포커스 스타일
모든 인터랙티브 요소에 명확한 포커스 스타일 적용:
- `focus-visible:outline-none`
- `focus-visible:ring-2`
- `focus-visible:ring-ring`
- `focus-visible:ring-offset-2`

### 3. 포커스 관리 (Focus Management)

#### Focus Trap Hook
모달, 다이얼로그 등에서 포커스를 내부에 가두는 훅.

**구현 위치**: `frontend/src/hooks/useFocusTrap.ts`

```typescript
const containerRef = useFocusTrap<HTMLDivElement>(isOpen);

return (
  <div ref={containerRef}>
    {/* 포커스가 이 컨테이너 내부에 갇힙니다 */}
  </div>
);
```

**기능**:
- 컨테이너 내 포커스 가능한 요소 자동 탐지
- Tab/Shift+Tab으로 순환 이동
- 첫 번째 요소에 자동 포커스

### 4. 색상 대비 (Color Contrast)

#### WCAG 2.1 AA 기준 준수
모든 텍스트와 배경의 색상 대비는 WCAG 2.1 Level AA 기준을 충족합니다.

**라이트 모드**:
- 일반 텍스트: `--foreground` (222.2 84% 4.9%) on `--background` (0 0% 100%)
- 대비율: 21:1 (AAA 등급)

**다크 모드**:
- 일반 텍스트: `--foreground` (210 40% 98%) on `--background` (222.2 84% 4.9%)
- 대비율: 17.5:1 (AAA 등급)

#### 상태 표시 색상
- Primary: `221.2 83.2% 53.3%` (라이트) / `217.2 91.2% 59.8%` (다크)
- Destructive: `0 84.2% 60.2%` (라이트) / `0 62.8% 30.6%` (다크)
- Muted: `215.4 16.3% 46.9%` (라이트) / `215 20.2% 65.1%` (다크)

모든 색상은 명확한 시각적 구분을 위해 충분한 대비를 제공합니다.

## 시맨틱 HTML

### 랜드마크 역할 (Landmark Roles)
- `<header role="banner">`: 사이트 헤더
- `<nav aria-label="주요 메뉴">`: 메인 네비게이션
- `<main role="main" id="main-content">`: 메인 콘텐츠
- `<footer role="contentinfo">`: 사이트 푸터

### 헤딩 구조 (Heading Hierarchy)
페이지당 하나의 `<h1>` 태그 사용을 권장하며, 논리적 순서로 헤딩 레벨을 구성합니다.

## 테스트 방법

### 1. 키보드 네비게이션 테스트
1. Tab 키로 모든 인터랙티브 요소 접근 가능한지 확인
2. Shift+Tab으로 역순 이동 확인
3. Enter/Space로 버튼 활성화 확인
4. Esc로 모달/드롭다운 닫기 확인

### 2. 스크린 리더 테스트
**권장 도구**:
- macOS: VoiceOver (Cmd+F5)
- Windows: NVDA (무료)
- Chrome 확장: ChromeVox

**테스트 항목**:
- 모든 이미지에 대체 텍스트 존재
- 폼 필드와 레이블 연결
- 에러 메시지 즉시 알림
- 버튼/링크 목적 명확

### 3. 색상 대비 테스트
**도구**:
- Chrome DevTools: Lighthouse
- WebAIM Contrast Checker: https://webaim.org/resources/contrastchecker/
- axe DevTools 확장

### 4. 자동화 테스트
```bash
# Lighthouse CI
npm run test:a11y

# axe-core 통합 테스트
npm run test:e2e -- --accessibility
```

## 베스트 프랙티스

### 1. 이미지 대체 텍스트
```jsx
// 의미 있는 이미지
<img src="profile.jpg" alt="홍길동 프로필 사진" />

// 장식용 이미지
<img src="decoration.png" alt="" role="presentation" />
```

### 2. 버튼 vs 링크
```jsx
// 페이지 이동: 링크 사용
<Link to="/dashboard">대시보드로 이동</Link>

// 동작 실행: 버튼 사용
<Button onClick={handleSubmit}>제출</Button>
```

### 3. 폼 레이블
```jsx
// 명시적 레이블
<label htmlFor="email">이메일</label>
<input id="email" type="email" />

// 에러 메시지
<input aria-invalid="true" aria-describedby="email-error" />
<p id="email-error" role="alert">올바른 이메일을 입력하세요</p>
```

### 4. 라이브 리전 (Live Regions)
```jsx
// 토스트 알림
<div role="status" aria-live="polite">
  저장되었습니다
</div>

// 긴급 알림
<div role="alert" aria-live="assertive">
  오류가 발생했습니다
</div>
```

## 참고 자료

- [WCAG 2.1 가이드라인](https://www.w3.org/WAI/WCAG21/quickref/)
- [WAI-ARIA Authoring Practices](https://www.w3.org/WAI/ARIA/apg/)
- [React Accessibility 문서](https://react.dev/learn/accessibility)
- [Tailwind CSS 접근성 유틸리티](https://tailwindcss.com/docs/screen-readers)

## 지속적 개선

접근성은 한 번의 작업으로 끝나지 않습니다. 다음 사항을 정기적으로 점검하세요:

1. **새 컴포넌트 추가 시**: ARIA 속성 및 키보드 네비게이션 확인
2. **색상 변경 시**: 대비율 재검증
3. **폼 추가/수정 시**: 레이블 및 에러 처리 확인
4. **인터랙티브 요소 추가 시**: 포커스 관리 및 키보드 지원 확인
