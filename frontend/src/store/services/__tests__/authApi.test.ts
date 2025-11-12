import { describe, it, expect, beforeEach } from "vitest";
import { configureStore } from "@reduxjs/toolkit";
import { authApi } from "../authApi";

describe("authApi", () => {
  let store: any;

  beforeEach(() => {
    store = configureStore({
      reducer: {
        [authApi.reducerPath]: authApi.reducer,
      },
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware().concat(authApi.middleware),
    });
  });

  describe("login", () => {
    it("should login successfully with valid credentials", async () => {
      const credentials = {
        email: "test@example.com",
        password: "password123",
      };

      const result = await store.dispatch(
        authApi.endpoints.login.initiate(credentials)
      );

      expect(result.data).toBeDefined();
      expect(result.data.accessToken).toBe("mock-access-token");
      expect(result.data.refreshToken).toBe("mock-refresh-token");
      expect(result.data.user).toBeDefined();
      expect(result.data.user.email).toBe("test@example.com");
      expect(result.data.user.name).toBe("테스트 사용자");
    });
  });

  describe("signup", () => {
    it("should signup successfully with valid data", async () => {
      const signupData = {
        email: "newuser@example.com",
        password: "Password123",
        passwordConfirm: "Password123",
        name: "새사용자",
        agreeToTerms: true,
      };

      const result = await store.dispatch(
        authApi.endpoints.signup.initiate(signupData)
      );

      expect(result.data).toBeDefined();
      expect(result.data.message).toBe("회원가입이 완료되었습니다");
    });
  });

  describe("logout", () => {
    it("should logout successfully", async () => {
      const result = await store.dispatch(authApi.endpoints.logout.initiate());

      expect(result.data).toBeDefined();
      expect(result.data.message).toBe("로그아웃되었습니다");
    });
  });
});
