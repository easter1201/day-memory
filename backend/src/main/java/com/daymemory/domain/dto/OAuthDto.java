package com.daymemory.domain.dto;

import com.daymemory.domain.entity.OAuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OAuthDto {

    @Schema(description = "OAuth 로그인 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuthLoginRequest {
        @Schema(description = "OAuth 인증 코드", example = "4/0AY0e-g7xxxxxxxxxxx")
        private String code;

        @Schema(description = "리다이렉트 URI", example = "http://localhost:3000/auth/callback")
        private String redirectUri;
    }

    @Schema(description = "OAuth 사용자 정보")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuthUserInfo {
        @Schema(description = "OAuth 제공자 사용자 ID", example = "123456789")
        private String providerId;

        @Schema(description = "이메일 주소", example = "user@gmail.com")
        private String email;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "OAuth 제공자 (GOOGLE, KAKAO, NAVER)", example = "GOOGLE")
        private OAuthProvider provider;
    }

    @Schema(description = "OAuth 로그인 응답")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuthLoginResponse {
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;

        @Schema(description = "사용자 ID", example = "1")
        private Long userId;

        @Schema(description = "이메일 주소", example = "user@gmail.com")
        private String email;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "신규 사용자 여부", example = "false")
        private boolean isNewUser;
    }
}
