import { http, HttpResponse } from "msw";

const API_BASE_URL = "http://localhost:8080/api";

export const handlers = [
  // Auth handlers
  http.post(`${API_BASE_URL}/auth/login`, () => {
    return HttpResponse.json({
      accessToken: "mock-access-token",
      refreshToken: "mock-refresh-token",
      user: {
        id: 1,
        email: "test@example.com",
        name: "테스트 사용자",
      },
    });
  }),

  http.post(`${API_BASE_URL}/auth/signup`, () => {
    return HttpResponse.json(
      {
        message: "회원가입이 완료되었습니다",
      },
      { status: 201 }
    );
  }),

  http.post(`${API_BASE_URL}/auth/logout`, () => {
    return HttpResponse.json({ message: "로그아웃되었습니다" });
  }),

  // Events handlers
  http.get(`${API_BASE_URL}/events`, () => {
    return HttpResponse.json({
      content: [
        {
          id: 1,
          title: "엄마 생일",
          eventDate: "2025-03-15",
          eventType: "BIRTHDAY",
          recipientName: "엄마",
          relationship: "FAMILY",
          dDay: "D-30",
        },
        {
          id: 2,
          title: "친구 결혼 기념일",
          eventDate: "2025-04-20",
          eventType: "ANNIVERSARY",
          recipientName: "친구",
          relationship: "FRIEND",
          dDay: "D-65",
        },
      ],
      totalPages: 1,
      totalElements: 2,
      number: 0,
    });
  }),

  http.get(`${API_BASE_URL}/events/:id`, ({ params }) => {
    const { id } = params;
    return HttpResponse.json({
      id: Number(id),
      title: "엄마 생일",
      eventDate: "2025-03-15",
      eventType: "BIRTHDAY",
      recipientName: "엄마",
      relationship: "FAMILY",
      memo: "선물 미리 준비하기",
      isTracked: true,
      reminders: [7, 3, 1],
      createdAt: "2025-01-15T10:00:00",
      updatedAt: "2025-01-15T10:00:00",
    });
  }),

  http.post(`${API_BASE_URL}/events`, () => {
    return HttpResponse.json(
      {
        id: 3,
        title: "새 이벤트",
        eventDate: "2025-05-01",
        eventType: "OTHER",
        recipientName: "테스트",
        relationship: "OTHER",
        isTracked: true,
      },
      { status: 201 }
    );
  }),

  http.put(`${API_BASE_URL}/events/:id`, ({ params }) => {
    const { id } = params;
    return HttpResponse.json({
      id: Number(id),
      title: "수정된 이벤트",
      eventDate: "2025-05-01",
      eventType: "BIRTHDAY",
      recipientName: "테스트",
      relationship: "FRIEND",
      isTracked: true,
    });
  }),

  http.delete(`${API_BASE_URL}/events/:id`, () => {
    return HttpResponse.json(null, { status: 204 });
  }),

  // Gifts handlers
  http.get(`${API_BASE_URL}/gifts`, () => {
    return HttpResponse.json({
      content: [
        {
          id: 1,
          name: "노트북",
          category: "ELECTRONICS",
          price: 1500000,
          url: "https://example.com/laptop",
          isPurchased: false,
          eventId: 1,
          eventTitle: "엄마 생일",
        },
      ],
      totalPages: 1,
      totalElements: 1,
      number: 0,
    });
  }),

  http.post(`${API_BASE_URL}/gifts`, () => {
    return HttpResponse.json(
      {
        id: 2,
        name: "새 선물",
        category: "FASHION",
        price: 100000,
        isPurchased: false,
      },
      { status: 201 }
    );
  }),

  // Recommendations handlers
  http.get(`${API_BASE_URL}/recommendations/event/:eventId`, () => {
    return HttpResponse.json([
      {
        id: 1,
        name: "추천 선물 1",
        category: "ELECTRONICS",
        estimatedPrice: 500000,
        reason: "AI가 추천하는 최적의 선물",
        confidence: 0.9,
      },
    ]);
  }),

  // Dashboard handlers
  http.get(`${API_BASE_URL}/dashboard/stats`, () => {
    return HttpResponse.json({
      upcomingEventsCount: 5,
      totalGiftsCount: 12,
      purchasedGiftsCount: 7,
      totalSpent: 2500000,
    });
  }),

  http.get(`${API_BASE_URL}/dashboard/upcoming-events`, () => {
    return HttpResponse.json([
      {
        id: 1,
        title: "엄마 생일",
        eventDate: "2025-03-15",
        recipientName: "엄마",
        dDay: "D-30",
      },
    ]);
  }),
];
