# PWA (Progressive Web App) 가이드

Day Memory의 PWA 구현 및 설정 가이드입니다.

## 구현된 PWA 기능

### 1. Web App Manifest

**파일**: `frontend/public/manifest.json`

웹 앱을 네이티브 앱처럼 설치하고 실행할 수 있게 해주는 메타데이터 파일입니다.

```json
{
  "name": "Day Memory",
  "short_name": "DayMemory",
  "description": "소중한 사람들의 특별한 날을 기억하고 관리하세요",
  "start_url": "/",
  "display": "standalone",
  "background_color": "#ffffff",
  "theme_color": "#3b82f6"
}
```

**주요 속성**:
- `name`: 앱의 전체 이름
- `short_name`: 홈 화면에 표시될 짧은 이름
- `start_url`: 앱이 시작될 URL
- `display`: 표시 모드 (standalone = 네이티브 앱처럼)
- `theme_color`: 브라우저 UI 색상
- `icons`: 다양한 크기의 앱 아이콘

**HTML 연결**:
```html
<link rel="manifest" href="/manifest.json" />
<meta name="theme-color" content="#3b82f6" />
<link rel="apple-touch-icon" href="/icons/icon-192x192.png" />
```

### 2. Service Worker

**파일**: `frontend/public/service-worker.js`

백그라운드에서 실행되어 오프라인 지원, 캐싱, 푸시 알림 등을 제공합니다.

#### 캐싱 전략

**정적 자산 (캐시 우선)**:
```javascript
// 앱 셸, CSS, JS 파일
// 캐시에 있으면 캐시에서 제공, 없으면 네트워크에서 가져옴
```

**API 요청 (네트워크 우선)**:
```javascript
// /api/* 요청
// 네트워크 우선, 실패 시 캐시에서 제공
```

#### 라이프사이클 이벤트

1. **Install**: 정적 자산 캐싱
2. **Activate**: 오래된 캐시 정리
3. **Fetch**: 요청 가로채기 및 캐시 처리

### 3. Service Worker 등록

**파일**: `frontend/src/utils/registerServiceWorker.ts`

프로덕션 환경에서만 Service Worker를 등록합니다.

```typescript
// main.tsx
if (import.meta.env.PROD) {
  registerServiceWorker();
}
```

**제공 기능**:
- `registerServiceWorker()`: Service Worker 등록
- `unregisterServiceWorker()`: Service Worker 해제
- `requestNotificationPermission()`: 알림 권한 요청
- `subscribeToPushNotifications()`: 푸시 알림 구독
- `unsubscribeFromPushNotifications()`: 푸시 알림 구독 해제

### 4. 오프라인 지원

#### 오프라인 감지 컴포넌트

**파일**: `frontend/src/components/common/OfflineNotice.tsx`

네트워크 연결 상태를 감지하고 오프라인 시 알림을 표시합니다.

```typescript
const [isOnline, setIsOnline] = useState(navigator.onLine);

useEffect(() => {
  window.addEventListener('online', handleOnline);
  window.addEventListener('offline', handleOffline);
}, []);
```

**특징**:
- `navigator.onLine` API 사용
- `online`/`offline` 이벤트 리스닝
- 화면 하단에 알림 표시
- ARIA 접근성 지원 (`role="alert"`, `aria-live="polite"`)

#### 캐시 전략

**앱 셸 캐싱**:
- `/`, `/index.html`
- `manifest.json`
- 앱 아이콘

**런타임 캐싱**:
- API 응답
- 동적으로 로드된 리소스

### 5. 푸시 알림

#### 알림 권한 요청

```typescript
const granted = await requestNotificationPermission();
```

#### 푸시 구독

```typescript
const subscription = await subscribeToPushNotifications();
// 서버에 구독 정보 전송
```

#### 알림 수신 (Service Worker)

```javascript
self.addEventListener('push', (event) => {
  const data = event.data.json();
  self.registration.showNotification(data.title, {
    body: data.body,
    icon: '/icons/icon-192x192.png',
    badge: '/icons/icon-72x72.png',
  });
});
```

#### 알림 클릭 처리

```javascript
self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  clients.openWindow(event.notification.data);
});
```

## 설치 방법

### Chrome (데스크톱)
1. 주소창 오른쪽의 설치 아이콘 클릭
2. "설치" 버튼 클릭

### Chrome (모바일)
1. 메뉴 (⋮) → "홈 화면에 추가"
2. 이름 확인 후 "추가" 버튼 클릭

### Safari (iOS)
1. 공유 버튼 탭
2. "홈 화면에 추가" 선택
3. 이름 확인 후 "추가" 버튼 탭

## 테스트 방법

### 1. Lighthouse로 PWA 점수 확인

Chrome DevTools → Lighthouse → Progressive Web App 항목 실행

**체크 항목**:
- ✅ Fast and reliable (오프라인 작동)
- ✅ Installable (설치 가능)
- ✅ PWA Optimized (최적화됨)

### 2. 오프라인 테스트

1. Chrome DevTools → Network → "Offline" 체크
2. 페이지 새로고침
3. 캐시된 콘텐츠 확인

### 3. Service Worker 상태 확인

Chrome DevTools → Application → Service Workers

**확인 사항**:
- Status: Activated and running
- Update on reload: 개발 중 체크
- Offline: 오프라인 테스트 시 체크

### 4. 캐시 검사

Chrome DevTools → Application → Cache Storage

**확인 항목**:
- `day-memory-v1`: 정적 자산 캐시
- `day-memory-runtime`: 런타임 캐시

## 환경 변수 설정

### VAPID 키 (푸시 알림용)

`.env` 파일에 추가:
```bash
VITE_VAPID_PUBLIC_KEY=your_vapid_public_key
```

**VAPID 키 생성** (백엔드):
```bash
npx web-push generate-vapid-keys
```

## 배포 시 주의사항

### 1. HTTPS 필수
PWA는 HTTPS 환경에서만 작동합니다 (localhost 제외).

### 2. Service Worker 업데이트
Service Worker가 업데이트되면 사용자에게 알림:
```typescript
if (confirm('새로운 버전이 있습니다. 페이지를 새로고침하시겠습니까?')) {
  window.location.reload();
}
```

### 3. 캐시 버전 관리
Service Worker 파일이 변경되면 캐시 버전을 업데이트:
```javascript
const CACHE_NAME = 'day-memory-v2'; // 버전 증가
```

### 4. 아이콘 준비
다양한 크기의 앱 아이콘을 `public/icons/` 폴더에 준비:
- 72x72, 96x96, 128x128, 144x144
- 152x152, 192x192, 384x384, 512x512

## 성능 최적화

### 1. 선택적 캐싱
자주 변경되지 않는 리소스만 캐시:
```javascript
const STATIC_ASSETS = [
  '/',
  '/index.html',
  '/manifest.json',
];
```

### 2. 캐시 크기 제한
오래된 캐시 항목 정리:
```javascript
const MAX_CACHE_SIZE = 50;
const MAX_CACHE_AGE = 7 * 24 * 60 * 60 * 1000; // 7일
```

### 3. 백그라운드 동기화
오프라인에서 생성된 데이터를 온라인 시 자동 동기화:
```javascript
self.addEventListener('sync', (event) => {
  if (event.tag === 'sync-data') {
    event.waitUntil(syncData());
  }
});
```

## 트러블슈팅

### Service Worker가 업데이트되지 않음
- Chrome DevTools → Application → Service Workers → "Update on reload" 체크
- 하드 리프레시 (Ctrl+Shift+R / Cmd+Shift+R)

### 오프라인에서 작동하지 않음
- 캐시에 필요한 자산이 포함되었는지 확인
- Network 탭에서 요청이 Service Worker에서 처리되는지 확인

### 푸시 알림이 작동하지 않음
- HTTPS 환경인지 확인
- 알림 권한이 허용되었는지 확인
- VAPID 키가 올바르게 설정되었는지 확인

## 참고 자료

- [PWA 체크리스트](https://web.dev/pwa-checklist/)
- [Service Worker API](https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API)
- [Web App Manifest](https://developer.mozilla.org/en-US/docs/Web/Manifest)
- [Push API](https://developer.mozilla.org/en-US/docs/Web/API/Push_API)
- [Workbox (Google의 PWA 라이브러리)](https://developers.google.com/web/tools/workbox)
