import { describe, it, expect, beforeEach, vi } from "vitest";
import {
  calculateDDay,
  getEventTypeBadgeColor,
  getEventTypeLabel,
  getRelationshipLabel,
  formatDate,
} from "../dateUtils";

describe("dateUtils", () => {
  describe("calculateDDay", () => {
    beforeEach(() => {
      // Mock current date to 2025-01-15
      vi.useFakeTimers();
      vi.setSystemTime(new Date("2025-01-15"));
    });

    it("should return D-Day for today", () => {
      expect(calculateDDay("2025-01-15")).toBe("D-Day");
    });

    it("should return D-N for future dates", () => {
      expect(calculateDDay("2025-01-20")).toBe("D-5");
      expect(calculateDDay("2025-01-16")).toBe("D-1");
    });

    it("should return D+N for past dates", () => {
      expect(calculateDDay("2025-01-10")).toBe("D+5");
      expect(calculateDDay("2025-01-14")).toBe("D+1");
    });

    afterEach(() => {
      vi.useRealTimers();
    });
  });

  describe("getEventTypeBadgeColor", () => {
    it("should return correct colors for known event types", () => {
      expect(getEventTypeBadgeColor("BIRTHDAY")).toBe("bg-blue-100 text-blue-800");
      expect(getEventTypeBadgeColor("ANNIVERSARY")).toBe("bg-pink-100 text-pink-800");
      expect(getEventTypeBadgeColor("HOLIDAY")).toBe("bg-green-100 text-green-800");
      expect(getEventTypeBadgeColor("OTHER")).toBe("bg-gray-100 text-gray-800");
    });

    it("should return default color for unknown event types", () => {
      expect(getEventTypeBadgeColor("UNKNOWN")).toBe("bg-gray-100 text-gray-800");
    });
  });

  describe("getEventTypeLabel", () => {
    it("should return correct labels for known event types", () => {
      expect(getEventTypeLabel("BIRTHDAY")).toBe("생일");
      expect(getEventTypeLabel("ANNIVERSARY")).toBe("기념일");
      expect(getEventTypeLabel("HOLIDAY")).toBe("명절");
      expect(getEventTypeLabel("OTHER")).toBe("기타");
    });

    it("should return the original value for unknown event types", () => {
      expect(getEventTypeLabel("CUSTOM_EVENT")).toBe("CUSTOM_EVENT");
    });
  });

  describe("getRelationshipLabel", () => {
    it("should return correct labels for known relationships", () => {
      expect(getRelationshipLabel("FAMILY")).toBe("가족");
      expect(getRelationshipLabel("FRIEND")).toBe("친구");
      expect(getRelationshipLabel("COLLEAGUE")).toBe("동료");
      expect(getRelationshipLabel("ACQUAINTANCE")).toBe("지인");
      expect(getRelationshipLabel("OTHER")).toBe("기타");
    });

    it("should return the original value for unknown relationships", () => {
      expect(getRelationshipLabel("CUSTOM_RELATION")).toBe("CUSTOM_RELATION");
    });

    it("should return '-' for undefined or empty relationship", () => {
      expect(getRelationshipLabel(undefined)).toBe("-");
      expect(getRelationshipLabel("")).toBe("-");
    });
  });

  describe("formatDate", () => {
    it("should format date in Korean locale", () => {
      const result = formatDate("2025-01-15");
      expect(result).toContain("2025");
      expect(result).toContain("1");
      expect(result).toContain("15");
    });

    it("should handle different date formats", () => {
      const result1 = formatDate("2025-12-25");
      const result2 = formatDate("2025-06-01");

      expect(result1).toBeTruthy();
      expect(result2).toBeTruthy();
    });
  });
});
