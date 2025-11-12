import { describe, it, expect } from "vitest";
import {
  loginSchema,
  signupSchema,
  eventSchema,
  giftSchema,
  passwordResetSchema,
  passwordChangeSchema,
} from "../validation";

describe("validation schemas", () => {
  describe("loginSchema", () => {
    it("should validate correct login data", () => {
      const validData = {
        email: "test@example.com",
        password: "password123",
      };
      expect(loginSchema.safeParse(validData).success).toBe(true);
    });

    it("should reject invalid email", () => {
      const invalidData = {
        email: "invalid-email",
        password: "password123",
      };
      const result = loginSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject short password", () => {
      const invalidData = {
        email: "test@example.com",
        password: "short",
      };
      const result = loginSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject empty fields", () => {
      const invalidData = {
        email: "",
        password: "",
      };
      const result = loginSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe("signupSchema", () => {
    it("should validate correct signup data", () => {
      const validData = {
        email: "test@example.com",
        password: "Password123",
        passwordConfirm: "Password123",
        name: "홍길동",
        agreeToTerms: true,
      };
      expect(signupSchema.safeParse(validData).success).toBe(true);
    });

    it("should reject password without uppercase", () => {
      const invalidData = {
        email: "test@example.com",
        password: "password123",
        passwordConfirm: "password123",
        name: "홍길동",
        agreeToTerms: true,
      };
      const result = signupSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject password without number", () => {
      const invalidData = {
        email: "test@example.com",
        password: "PasswordTest",
        passwordConfirm: "PasswordTest",
        name: "홍길동",
        agreeToTerms: true,
      };
      const result = signupSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject mismatched passwords", () => {
      const invalidData = {
        email: "test@example.com",
        password: "Password123",
        passwordConfirm: "Password456",
        name: "홍길동",
        agreeToTerms: true,
      };
      const result = signupSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject short name", () => {
      const invalidData = {
        email: "test@example.com",
        password: "Password123",
        passwordConfirm: "Password123",
        name: "김",
        agreeToTerms: true,
      };
      const result = signupSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject without terms agreement", () => {
      const invalidData = {
        email: "test@example.com",
        password: "Password123",
        passwordConfirm: "Password123",
        name: "홍길동",
        agreeToTerms: false,
      };
      const result = signupSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe("eventSchema", () => {
    it("should validate correct event data", () => {
      const validData = {
        title: "생일 파티",
        eventDate: "2025-12-25",
        eventType: "BIRTHDAY",
        recipientName: "홍길동",
        relationship: "FRIEND",
        memo: "선물 준비하기",
        isTracked: true,
        reminders: [7, 3, 1],
      };
      expect(eventSchema.safeParse(validData).success).toBe(true);
    });

    it("should reject empty required fields", () => {
      const invalidData = {
        title: "",
        eventDate: "",
        eventType: "",
        recipientName: "",
        isTracked: false,
      };
      const result = eventSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject too long title", () => {
      const invalidData = {
        title: "a".repeat(101),
        eventDate: "2025-12-25",
        eventType: "BIRTHDAY",
        recipientName: "홍길동",
        isTracked: true,
      };
      const result = eventSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should allow optional fields to be undefined", () => {
      const validData = {
        title: "생일 파티",
        eventDate: "2025-12-25",
        eventType: "BIRTHDAY",
        recipientName: "홍길동",
        isTracked: true,
      };
      expect(eventSchema.safeParse(validData).success).toBe(true);
    });
  });

  describe("giftSchema", () => {
    it("should validate correct gift data", () => {
      const validData = {
        name: "노트북",
        category: "ELECTRONICS",
        price: 1500000,
        url: "https://example.com/product",
        memo: "최신 모델",
        isPurchased: false,
        eventId: 1,
      };
      expect(giftSchema.safeParse(validData).success).toBe(true);
    });

    it("should reject negative price", () => {
      const invalidData = {
        name: "노트북",
        category: "ELECTRONICS",
        price: -1000,
        isPurchased: false,
      };
      const result = giftSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject invalid URL", () => {
      const invalidData = {
        name: "노트북",
        category: "ELECTRONICS",
        price: 1500000,
        url: "not-a-url",
        isPurchased: false,
      };
      const result = giftSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should allow empty URL string", () => {
      const validData = {
        name: "노트북",
        category: "ELECTRONICS",
        price: 1500000,
        url: "",
        isPurchased: false,
      };
      expect(giftSchema.safeParse(validData).success).toBe(true);
    });

    it("should reject price over limit", () => {
      const invalidData = {
        name: "노트북",
        category: "ELECTRONICS",
        price: 100000001,
        isPurchased: false,
      };
      const result = giftSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe("passwordResetSchema", () => {
    it("should validate correct email", () => {
      const validData = {
        email: "test@example.com",
      };
      expect(passwordResetSchema.safeParse(validData).success).toBe(true);
    });

    it("should reject invalid email", () => {
      const invalidData = {
        email: "invalid-email",
      };
      const result = passwordResetSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });

  describe("passwordChangeSchema", () => {
    it("should validate correct password change data", () => {
      const validData = {
        password: "NewPassword123",
        passwordConfirm: "NewPassword123",
      };
      expect(passwordChangeSchema.safeParse(validData).success).toBe(true);
    });

    it("should reject mismatched passwords", () => {
      const invalidData = {
        password: "NewPassword123",
        passwordConfirm: "DifferentPassword123",
      };
      const result = passwordChangeSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });

    it("should reject weak password", () => {
      const invalidData = {
        password: "weakpass",
        passwordConfirm: "weakpass",
      };
      const result = passwordChangeSchema.safeParse(invalidData);
      expect(result.success).toBe(false);
    });
  });
});
