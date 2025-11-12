const CACHE_NAME = "day-memory-v1";
const RUNTIME_CACHE = "day-memory-runtime";

// 캐시할 정적 자산
const STATIC_ASSETS = [
  "/",
  "/index.html",
  "/manifest.json",
  "/icons/icon-192x192.png",
  "/icons/icon-512x512.png",
];

// 설치 이벤트: 정적 자산 캐싱
self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log("[Service Worker] Caching static assets");
      return cache.addAll(STATIC_ASSETS);
    })
  );
  self.skipWaiting();
});

// 활성화 이벤트: 오래된 캐시 정리
self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames
          .filter((name) => name !== CACHE_NAME && name !== RUNTIME_CACHE)
          .map((name) => {
            console.log("[Service Worker] Deleting old cache:", name);
            return caches.delete(name);
          })
      );
    })
  );
  self.clients.claim();
});

// Fetch 이벤트: 네트워크 우선, 캐시 폴백 전략
self.addEventListener("fetch", (event) => {
  const { request } = event;
  const url = new URL(request.url);

  // API 요청은 네트워크 우선
  if (url.pathname.startsWith("/api")) {
    event.respondWith(
      fetch(request)
        .then((response) => {
          // 성공한 응답을 런타임 캐시에 저장
          const responseClone = response.clone();
          caches.open(RUNTIME_CACHE).then((cache) => {
            cache.put(request, responseClone);
          });
          return response;
        })
        .catch(() => {
          // 네트워크 실패 시 캐시에서 가져오기
          return caches.match(request);
        })
    );
    return;
  }

  // 정적 자산은 캐시 우선
  event.respondWith(
    caches.match(request).then((cachedResponse) => {
      if (cachedResponse) {
        return cachedResponse;
      }

      // 캐시에 없으면 네트워크에서 가져오기
      return fetch(request).then((response) => {
        // 유효한 응답이면 런타임 캐시에 저장
        if (!response || response.status !== 200 || response.type === "error") {
          return response;
        }

        const responseClone = response.clone();
        caches.open(RUNTIME_CACHE).then((cache) => {
          cache.put(request, responseClone);
        });

        return response;
      });
    })
  );
});

// 푸시 알림 수신
self.addEventListener("push", (event) => {
  const data = event.data ? event.data.json() : {};
  const title = data.title || "Day Memory";
  const options = {
    body: data.body || "새로운 알림이 도착했습니다",
    icon: "/icons/icon-192x192.png",
    badge: "/icons/icon-72x72.png",
    data: data.url || "/",
    actions: [
      {
        action: "open",
        title: "열기",
      },
      {
        action: "close",
        title: "닫기",
      },
    ],
  };

  event.waitUntil(self.registration.showNotification(title, options));
});

// 알림 클릭 이벤트
self.addEventListener("notificationclick", (event) => {
  event.notification.close();

  if (event.action === "open" || !event.action) {
    event.waitUntil(
      clients.openWindow(event.notification.data || "/")
    );
  }
});

// 백그라운드 동기화 (오프라인에서 작성한 데이터 동기화)
self.addEventListener("sync", (event) => {
  if (event.tag === "sync-data") {
    event.waitUntil(syncData());
  }
});

async function syncData() {
  // IndexedDB나 localStorage에 저장된 오프라인 데이터를 서버로 동기화
  console.log("[Service Worker] Syncing offline data");
  // 실제 동기화 로직 구현
}
