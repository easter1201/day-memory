# UI/UX 상세 가이드 (UI/UX Detailed Guide) - Day Memory

## 📋 개요
Day Memory의 디자인 시스템과 UI 정책을 정의하는 문서입니다. 컬러, 폰트, 버튼 스타일, 반응형 규칙, 접근성 지침 등을 포함하여 일관성 있는 UI 구현을 보장합니다.

---

## 🎨 컬러 팔레트

### Primary Colors
```css
--primary-50: #EFF6FF;    /* 가장 밝은 Primary */
--primary-100: #DBEAFE;
--primary-200: #BFDBFE;
--primary-300: #93C5FD;
--primary-400: #60A5FA;
--primary-500: #3B82F6;   /* 메인 Primary */
--primary-600: #2563EB;   /* Primary 기본값 */
--primary-700: #1D4ED8;
--primary-800: #1E40AF;
--primary-900: #1E3A8A;
```

### Secondary Colors
```css
--secondary-50: #FAF5FF;
--secondary-100: #F3E8FF;
--secondary-200: #E9D5FF;
--secondary-300: #D8B4FE;
--secondary-400: #C084FC;
--secondary-500: #A855F7;  /* 메인 Secondary */
--secondary-600: #9333EA;  /* Secondary 기본값 */
--secondary-700: #7E22CE;
--secondary-800: #6B21A8;
--secondary-900: #581C87;
```

### Neutral Colors (배경, 텍스트)
```css
--gray-50: #F9FAFB;       /* 밝은 배경 */
--gray-100: #F3F4F6;      /* 카드 배경 */
--gray-200: #E5E7EB;      /* Border */
--gray-300: #D1D5DB;
--gray-400: #9CA3AF;
--gray-500: #6B7280;
--gray-600: #4B5563;      /* 보조 텍스트 */
--gray-700: #374151;
--gray-800: #1F2937;      /* 주요 텍스트 */
--gray-900: #111827;      /* 진한 텍스트 */
```

### Semantic Colors
```css
--success: #10B981;       /* 성공 메시지, 완료 상태 */
--warning: #F59E0B;       /* 경고 메시지 */
--error: #EF4444;         /* 에러 메시지, 삭제 버튼 */
--info: #3B82F6;          /* 정보 메시지 */
```

### Background & Text
```css
--bg-primary: #FFFFFF;    /* 메인 배경 */
--bg-secondary: #F9FAFB;  /* 서브 배경 */
--bg-tertiary: #F3F4F6;   /* 카드 배경 */
--text-primary: #111827;  /* 주요 텍스트 */
--text-secondary: #4B5563;/* 보조 텍스트 */
--text-disabled: #9CA3AF; /* 비활성 텍스트 */
```

---

## 🔤 타이포그래피

### 폰트 패밀리
```css
--font-primary: 'Pretendard', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
--font-secondary: 'Inter', sans-serif;
--font-monospace: 'Fira Code', 'Courier New', monospace;
```

### 폰트 크기
```css
--text-xs: 12px;          /* 작은 텍스트, 라벨 */
--text-sm: 14px;          /* 본문 */
--text-base: 16px;        /* 기본 본문 */
--text-lg: 18px;          /* 강조 텍스트 */
--text-xl: 20px;          /* 서브 타이틀 */
--text-2xl: 24px;         /* 페이지 타이틀 */
--text-3xl: 30px;         /* 대형 타이틀 */
--text-4xl: 36px;         /* 히어로 타이틀 */
```

### 폰트 무게
```css
--font-light: 300;
--font-regular: 400;
--font-medium: 500;
--font-semibold: 600;
--font-bold: 700;
```

### 행간 (Line Height)
```css
--leading-tight: 1.25;    /* 타이틀 */
--leading-normal: 1.5;    /* 본문 */
--leading-relaxed: 1.75;  /* 긴 본문 */
```

### 텍스트 스타일 예시
```css
/* H1 - 페이지 타이틀 */
h1 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  line-height: var(--leading-tight);
  color: var(--text-primary);
}

/* H2 - 섹션 타이틀 */
h2 {
  font-size: var(--text-xl);
  font-weight: var(--font-semibold);
  line-height: var(--leading-tight);
  color: var(--text-primary);
}

/* Body - 본문 */
body {
  font-size: var(--text-base);
  font-weight: var(--font-regular);
  line-height: var(--leading-normal);
  color: var(--text-primary);
}

/* Small - 보조 텍스트 */
small {
  font-size: var(--text-sm);
  color: var(--text-secondary);
}
```

---

## 🔘 버튼 스타일

### Primary Button
```css
.btn-primary {
  background-color: var(--primary-600);
  color: #FFFFFF;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-primary:hover {
  background-color: var(--primary-700);
}

.btn-primary:active {
  background-color: var(--primary-800);
}

.btn-primary:disabled {
  background-color: var(--gray-300);
  cursor: not-allowed;
}
```

### Secondary Button
```css
.btn-secondary {
  background-color: transparent;
  color: var(--primary-600);
  padding: 12px 24px;
  border-radius: 8px;
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  border: 2px solid var(--primary-600);
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background-color: var(--primary-50);
}
```

### Ghost Button (텍스트 버튼)
```css
.btn-ghost {
  background-color: transparent;
  color: var(--text-primary);
  padding: 8px 16px;
  border: none;
  cursor: pointer;
  font-weight: var(--font-medium);
  transition: background-color 0.2s ease;
}

.btn-ghost:hover {
  background-color: var(--gray-100);
}
```

### Danger Button (삭제 버튼)
```css
.btn-danger {
  background-color: var(--error);
  color: #FFFFFF;
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: var(--font-medium);
  border: none;
  cursor: pointer;
}

.btn-danger:hover {
  background-color: #DC2626;
}
```

### 버튼 크기
```css
.btn-sm {
  padding: 8px 16px;
  font-size: var(--text-sm);
}

.btn-md {
  padding: 12px 24px;
  font-size: var(--text-base);
}

.btn-lg {
  padding: 16px 32px;
  font-size: var(--text-lg);
}
```

---

## 📦 카드 스타일

### 기본 카드
```css
.card {
  background-color: var(--bg-primary);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s ease;
}

.card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
```

### 이벤트 카드
```css
.event-card {
  background-color: var(--bg-primary);
  border-radius: 12px;
  padding: 20px;
  border-left: 4px solid var(--primary-600);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
```

### 선물 카드
```css
.gift-card {
  background-color: var(--bg-primary);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--gray-200);
}

.gift-card.purchased {
  background-color: var(--gray-50);
  opacity: 0.7;
}
```

---

## 🎛️ 폼 (Form) 스타일

### Input Field
```css
.input {
  width: 100%;
  padding: 12px 16px;
  font-size: var(--text-base);
  border: 1px solid var(--gray-300);
  border-radius: 8px;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  transition: border-color 0.2s ease;
}

.input:focus {
  outline: none;
  border-color: var(--primary-600);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.input:disabled {
  background-color: var(--gray-100);
  cursor: not-allowed;
}

.input.error {
  border-color: var(--error);
}
```

### Textarea
```css
.textarea {
  width: 100%;
  padding: 12px 16px;
  font-size: var(--text-base);
  border: 1px solid var(--gray-300);
  border-radius: 8px;
  resize: vertical;
  min-height: 100px;
}
```

### Select (Dropdown)
```css
.select {
  width: 100%;
  padding: 12px 16px;
  font-size: var(--text-base);
  border: 1px solid var(--gray-300);
  border-radius: 8px;
  background-color: var(--bg-primary);
  cursor: pointer;
}
```

### Checkbox & Radio
```css
.checkbox,
.radio {
  width: 20px;
  height: 20px;
  accent-color: var(--primary-600);
  cursor: pointer;
}
```

### Label
```css
.label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-primary);
  margin-bottom: 8px;
}
```

---

## 📱 반응형 규칙

### Breakpoints
```css
--mobile: 640px;          /* < 640px */
--tablet: 768px;          /* 640px ~ 1024px */
--desktop: 1024px;        /* > 1024px */
--wide: 1280px;           /* > 1280px */
```

### 레이아웃 규칙
```css
/* Mobile: 1단 레이아웃 */
@media (max-width: 640px) {
  .grid {
    grid-template-columns: 1fr;
  }
}

/* Tablet: 2단 레이아웃 */
@media (min-width: 641px) and (max-width: 1024px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* Desktop: 3~4단 레이아웃 */
@media (min-width: 1025px) {
  .grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
```

### 반응형 폰트 크기
```css
/* Mobile */
h1 {
  font-size: 24px;
}

/* Tablet */
@media (min-width: 641px) {
  h1 {
    font-size: 30px;
  }
}

/* Desktop */
@media (min-width: 1025px) {
  h1 {
    font-size: 36px;
  }
}
```

---

## 🎭 애니메이션 & 트랜지션

### 기본 트랜지션
```css
--transition-fast: 0.15s ease;
--transition-base: 0.2s ease;
--transition-slow: 0.3s ease;
```

### 페이드 인
```css
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.fade-in {
  animation: fadeIn 0.3s ease;
}
```

### 슬라이드 인 (위에서 아래로)
```css
@keyframes slideDown {
  from {
    transform: translateY(-10px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.slide-down {
  animation: slideDown 0.3s ease;
}
```

### 호버 효과
```css
.hover-lift {
  transition: transform 0.2s ease;
}

.hover-lift:hover {
  transform: translateY(-4px);
}
```

---

## 🧭 네비게이션 스타일

### Header
```css
.header {
  background-color: var(--bg-primary);
  border-bottom: 1px solid var(--gray-200);
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}
```

### Sidebar
```css
.sidebar {
  width: 256px;
  background-color: var(--bg-secondary);
  border-right: 1px solid var(--gray-200);
  padding: 24px 16px;
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
}

.sidebar-link {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  color: var(--text-primary);
  font-weight: var(--font-medium);
  transition: background-color 0.2s ease;
}

.sidebar-link:hover {
  background-color: var(--gray-100);
}

.sidebar-link.active {
  background-color: var(--primary-50);
  color: var(--primary-600);
}
```

---

## 🔔 알림 & 모달

### Toast Notification
```css
.toast {
  background-color: var(--bg-primary);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 12px;
  animation: slideDown 0.3s ease;
}

.toast.success {
  border-left: 4px solid var(--success);
}

.toast.error {
  border-left: 4px solid var(--error);
}
```

### Modal
```css
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background-color: var(--bg-primary);
  border-radius: 12px;
  padding: 24px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.3);
  animation: fadeIn 0.2s ease;
}
```

---

## ♿ 접근성 (Accessibility)

### 포커스 스타일
```css
*:focus-visible {
  outline: 2px solid var(--primary-600);
  outline-offset: 2px;
}
```

### 키보드 내비게이션
- 모든 인터랙티브 요소는 키보드로 접근 가능해야 함
- Tab 순서가 논리적이어야 함
- Enter 또는 Space로 버튼 활성화 가능

### ARIA 속성
```html
<!-- 버튼 -->
<button aria-label="이벤트 삭제">삭제</button>

<!-- 로딩 상태 -->
<div role="status" aria-live="polite">
  데이터를 불러오는 중...
</div>

<!-- 에러 메시지 -->
<div role="alert" aria-live="assertive">
  이메일 형식이 올바르지 않습니다.
</div>
```

### 색상 대비
- WCAG 2.1 AA 기준: 최소 4.5:1 대비율
- 주요 텍스트와 배경 간 충분한 대비 유지

---

## 🎨 레이아웃 시스템

### Container
```css
.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

/* Mobile */
@media (max-width: 640px) {
  .container {
    padding: 0 16px;
  }
}
```

### Grid System
```css
.grid {
  display: grid;
  gap: 24px;
}

.grid-cols-1 { grid-template-columns: repeat(1, 1fr); }
.grid-cols-2 { grid-template-columns: repeat(2, 1fr); }
.grid-cols-3 { grid-template-columns: repeat(3, 1fr); }
.grid-cols-4 { grid-template-columns: repeat(4, 1fr); }
```

### Spacing
```css
--spacing-xs: 4px;
--spacing-sm: 8px;
--spacing-md: 16px;
--spacing-lg: 24px;
--spacing-xl: 32px;
--spacing-2xl: 48px;
```

---

## 🎯 아이콘 가이드

### 아이콘 라이브러리
- **Heroicons** (https://heroicons.com/) 또는 **Lucide** (https://lucide.dev/)
- 일관된 라인 두께 및 스타일 유지

### 아이콘 크기
```css
.icon-xs { width: 16px; height: 16px; }
.icon-sm { width: 20px; height: 20px; }
.icon-md { width: 24px; height: 24px; }
.icon-lg { width: 32px; height: 32px; }
.icon-xl { width: 48px; height: 48px; }
```

---

## 📏 간격 및 여백 규칙

### 일관된 간격 사용
- 섹션 간 여백: `32px` 또는 `48px`
- 카드 간 간격: `16px` 또는 `24px`
- 텍스트 간 여백: `8px` 또는 `12px`

### 패딩 규칙
- 카드 내부 패딩: `20px`
- 버튼 패딩: `12px 24px`
- 입력 필드 패딩: `12px 16px`

---

## 🌓 다크 모드 (선택 사항)

### 다크 모드 컬러
```css
[data-theme="dark"] {
  --bg-primary: #1F2937;
  --bg-secondary: #111827;
  --text-primary: #F9FAFB;
  --text-secondary: #D1D5DB;
  --gray-200: #374151;
}
```

---

**작성일**: 2025-01-11
**버전**: 1.0
**참고**: project_idea.md, Tailwind CSS 3, shadcn/ui 기반 작성
