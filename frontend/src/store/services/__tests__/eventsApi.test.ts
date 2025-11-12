import { describe, it, expect, beforeEach } from "vitest";
import { configureStore } from "@reduxjs/toolkit";
import { eventsApi } from "../eventsApi";

describe("eventsApi", () => {
  let store: any;

  beforeEach(() => {
    store = configureStore({
      reducer: {
        [eventsApi.reducerPath]: eventsApi.reducer,
      },
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware().concat(eventsApi.middleware),
    });
  });

  describe("getEvents", () => {
    it("should fetch events successfully", async () => {
      const result = await store.dispatch(
        eventsApi.endpoints.getEvents.initiate({ page: 0, size: 10 })
      );

      expect(result.data).toBeDefined();
      expect(result.data.content).toHaveLength(2);
      expect(result.data.content[0].title).toBe("엄마 생일");
      expect(result.data.content[1].title).toBe("친구 결혼 기념일");
    });

    it("should return paginated data", async () => {
      const result = await store.dispatch(
        eventsApi.endpoints.getEvents.initiate({ page: 0, size: 10 })
      );

      expect(result.data.totalPages).toBe(1);
      expect(result.data.totalElements).toBe(2);
      expect(result.data.number).toBe(0);
    });
  });

  describe("getEventById", () => {
    it("should fetch a single event by id", async () => {
      const result = await store.dispatch(
        eventsApi.endpoints.getEventById.initiate(1)
      );

      expect(result.data).toBeDefined();
      expect(result.data.id).toBe(1);
      expect(result.data.title).toBe("엄마 생일");
      expect(result.data.eventType).toBe("BIRTHDAY");
      expect(result.data.recipientName).toBe("엄마");
    });

    it("should include reminders data", async () => {
      const result = await store.dispatch(
        eventsApi.endpoints.getEventById.initiate(1)
      );

      expect(result.data.reminders).toEqual([7, 3, 1]);
      expect(result.data.isTracked).toBe(true);
    });
  });

  describe("createEvent", () => {
    it("should create a new event", async () => {
      const newEvent = {
        title: "새 이벤트",
        eventDate: "2025-05-01",
        eventType: "OTHER",
        recipientName: "테스트",
        relationship: "OTHER",
        isTracked: true,
      };

      const result = await store.dispatch(
        eventsApi.endpoints.createEvent.initiate(newEvent)
      );

      expect(result.data).toBeDefined();
      expect(result.data.id).toBe(3);
      expect(result.data.title).toBe("새 이벤트");
    });
  });

  describe("updateEvent", () => {
    it("should update an existing event", async () => {
      const updateData = {
        title: "수정된 이벤트",
        eventDate: "2025-05-01",
        eventType: "BIRTHDAY",
        recipientName: "테스트",
        relationship: "FRIEND",
        isTracked: true,
      };

      const result = await store.dispatch(
        eventsApi.endpoints.updateEvent.initiate({ id: 1, data: updateData })
      );

      expect(result.data).toBeDefined();
      expect(result.data.title).toBe("수정된 이벤트");
      expect(result.data.eventType).toBe("BIRTHDAY");
    });
  });

  describe("deleteEvent", () => {
    it("should delete an event", async () => {
      const result = await store.dispatch(
        eventsApi.endpoints.deleteEvent.initiate(1)
      );

      expect(result.data).toBeNull();
    });
  });
});
