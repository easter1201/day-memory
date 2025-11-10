package com.daymemory.domain.dto;

import com.daymemory.domain.entity.OAuthProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OAuthDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuthLoginRequest {
        private String code;  // Authorization code
        private String redirectUri;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuthUserInfo {
        private String providerId;
        private String email;
        private String name;
        private OAuthProvider provider;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OAuthLoginResponse {
        private String accessToken;
        private String refreshToken;
        private Long userId;
        private String email;
        private String name;
        private boolean isNewUser;
    }
}
