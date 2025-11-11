package com.daymemory.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class VerificationDto {

    @Schema(description = "이메일 인증 발송 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendEmailVerificationRequest {
        @Schema(description = "인증 이메일을 받을 이메일 주소", example = "user@example.com")
        private String email;
    }

    @Schema(description = "이메일 인증 확인 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerifyEmailRequest {
        @Schema(description = "이메일 인증 토큰", example = "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6")
        private String token;
    }

    @Schema(description = "비밀번호 재설정 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasswordResetRequest {
        @Schema(description = "비밀번호를 재설정할 이메일 주소", example = "user@example.com")
        private String email;
    }

    @Schema(description = "비밀번호 재설정 확인 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasswordResetConfirmRequest {
        @Schema(description = "비밀번호 재설정 토큰", example = "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6")
        private String token;

        @Schema(description = "새 비밀번호", example = "newpassword123")
        private String newPassword;
    }

    @Schema(description = "인증 요청 응답")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        @Schema(description = "응답 메시지", example = "인증 이메일이 발송되었습니다.")
        private String message;

        @Schema(description = "성공 여부", example = "true")
        private Boolean success;
    }
}
