/**
 * Service Worker 등록 유틸리티
 */

export function registerServiceWorker() {
  if (!("serviceWorker" in navigator)) {
    console.log("Service Worker not supported");
    return;
  }

  window.addEventListener("load", async () => {
    try {
      const registration = await navigator.serviceWorker.register("/service-worker.js", {
        scope: "/",
      });

      console.log("Service Worker registered:", registration.scope);

      // 업데이트 확인
      registration.addEventListener("updatefound", () => {
        const newWorker = registration.installing;
        if (!newWorker) return;

        newWorker.addEventListener("statechange", () => {
          if (newWorker.state === "installed" && navigator.serviceWorker.controller) {
            // 새 버전이 설치됨 - 사용자에게 알림
            console.log("New content available; please refresh.");

            // 선택적: 사용자에게 리프레시 프롬프트 표시
            if (confirm("새로운 버전이 있습니다. 페이지를 새로고침하시겠습니까?")) {
              window.location.reload();
            }
          }
        });
      });

      // 주기적으로 업데이트 확인 (1시간마다)
      setInterval(() => {
        registration.update();
      }, 60 * 60 * 1000);
    } catch (error) {
      console.error("Service Worker registration failed:", error);
    }
  });
}

/**
 * Service Worker 해제
 */
export async function unregisterServiceWorker() {
  if (!("serviceWorker" in navigator)) {
    return false;
  }

  try {
    const registration = await navigator.serviceWorker.getRegistration();
    if (registration) {
      await registration.unregister();
      console.log("Service Worker unregistered");
      return true;
    }
    return false;
  } catch (error) {
    console.error("Service Worker unregistration failed:", error);
    return false;
  }
}

/**
 * 푸시 알림 권한 요청
 */
export async function requestNotificationPermission() {
  if (!("Notification" in window)) {
    console.log("Notifications not supported");
    return false;
  }

  if (Notification.permission === "granted") {
    return true;
  }

  if (Notification.permission !== "denied") {
    const permission = await Notification.requestPermission();
    return permission === "granted";
  }

  return false;
}

/**
 * 푸시 알림 구독
 */
export async function subscribeToPushNotifications() {
  if (!("serviceWorker" in navigator) || !("PushManager" in window)) {
    console.log("Push notifications not supported");
    return null;
  }

  try {
    const registration = await navigator.serviceWorker.ready;

    // VAPID 공개 키 (실제 프로젝트에서는 환경변수로 관리)
    const vapidPublicKey = import.meta.env.VITE_VAPID_PUBLIC_KEY || "";

    if (!vapidPublicKey) {
      console.warn("VAPID public key not configured");
      return null;
    }

    const subscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlBase64ToUint8Array(vapidPublicKey),
    });

    console.log("Push subscription:", JSON.stringify(subscription));

    // 서버에 구독 정보 전송
    await sendSubscriptionToServer(subscription);

    return subscription;
  } catch (error) {
    console.error("Failed to subscribe to push notifications:", error);
    return null;
  }
}

/**
 * 푸시 알림 구독 해제
 */
export async function unsubscribeFromPushNotifications() {
  if (!("serviceWorker" in navigator)) {
    return false;
  }

  try {
    const registration = await navigator.serviceWorker.ready;
    const subscription = await registration.pushManager.getSubscription();

    if (subscription) {
      await subscription.unsubscribe();
      console.log("Push subscription cancelled");

      // 서버에 구독 해제 알림
      await removeSubscriptionFromServer(subscription);

      return true;
    }
    return false;
  } catch (error) {
    console.error("Failed to unsubscribe from push notifications:", error);
    return false;
  }
}

// Helper functions

function urlBase64ToUint8Array(base64String: string) {
  const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, "+").replace(/_/g, "/");
  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }

  return outputArray;
}

async function sendSubscriptionToServer(subscription: PushSubscription) {
  // 실제 구현: 서버 API 호출
  console.log("Sending subscription to server:", subscription.toJSON());

  try {
    const response = await fetch("/api/push/subscribe", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(subscription.toJSON()),
    });

    if (!response.ok) {
      throw new Error("Failed to send subscription to server");
    }
  } catch (error) {
    console.error("Error sending subscription to server:", error);
  }
}

async function removeSubscriptionFromServer(subscription: PushSubscription) {
  // 실제 구현: 서버 API 호출
  console.log("Removing subscription from server:", subscription.toJSON());

  try {
    const response = await fetch("/api/push/unsubscribe", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(subscription.toJSON()),
    });

    if (!response.ok) {
      throw new Error("Failed to remove subscription from server");
    }
  } catch (error) {
    console.error("Error removing subscription from server:", error);
  }
}
