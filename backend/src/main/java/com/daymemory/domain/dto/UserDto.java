package com.daymemory.domain.dto;

import com.daymemory.domain.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {

    /**
     * 회원가입 요청 DTO
     */
    @Schema(description = "회원가입 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRequest {
        @Schema(description = "이메일 주소", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @Schema(description = "비밀번호 (8~20자)", example = "password1234", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내로 입력해주세요.")
        private String password;

        @Schema(description = "닉네임", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2~20자 이내로 입력해주세요.")
        private String nickname;
    }

    /**
     * 로그인 요청 DTO
     */
    @Schema(description = "로그인 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @Schema(description = "이메일 주소", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @Schema(description = "비밀번호", example = "password1234", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String password;
    }

    /**
     * 로그인 응답 DTO
     */
    @Schema(description = "로그인 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;

        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String refreshToken;

        @Schema(description = "사용자 정보")
        private Response user;
    }

    /**
     * 토큰 재발급 요청 DTO
     */
    @Schema(description = "토큰 재발급 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshRequest {
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Refresh Token은 필수입니다.")
        private String refreshToken;
    }

    /**
     * 토큰 재발급 응답 DTO
     */
    @Schema(description = "토큰 재발급 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshResponse {
        @Schema(description = "새로운 액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String accessToken;
    }

    /**
     * 비밀번호 변경 요청 DTO
     */
    @Schema(description = "비밀번호 변경 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordChangeRequest {
        @Schema(description = "현재 비밀번호", example = "oldpassword123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "현재 비밀번호는 필수입니다.")
        private String currentPassword;

        @Schema(description = "새 비밀번호 (8~20자)", example = "newpassword456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내로 입력해주세요.")
        private String newPassword;
    }

    /**
     * 사용자 정보 수정 요청 DTO
     */
    @Schema(description = "사용자 정보 수정 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @Schema(description = "변경할 닉네임", example = "김철수")
        @Size(min = 2, max = 20, message = "닉네임은 2~20자 이내로 입력해주세요.")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        private String profileImageUrl;
    }

    /**
     * 사용자 응답 DTO
     */
    @Schema(description = "사용자 정보 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "이메일 주소", example = "user@example.com")
        private String email;

        @Schema(description = "닉네임", example = "홍길동")
        private String nickname;

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
        private String profileImageUrl;

        @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
        private String createdAt;

        @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
        private String updatedAt;

        public static Response from(User user) {
            return Response.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImageUrl(user.getProfileImageUrl())
                    .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                    .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null)
                    .build();
        }
    }

    /**
     * 알림 설정 요청 DTO
     */
    @Schema(description = "알림 설정 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSettingsRequest {
        @Schema(description = "이메일 알림 활성화 여부", example = "true")
        private Boolean emailNotificationsEnabled;

        @Schema(description = "알림 시간 (HH:mm 형식)", example = "09:00")
        private String reminderTime;
    }

    /**
     * 알림 설정 응답 DTO
     */
    @Schema(description = "알림 설정 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSettingsResponse {
        @Schema(description = "이메일 알림 활성화 여부", example = "true")
        private Boolean emailNotificationsEnabled;

        @Schema(description = "알림 시간 (HH:mm 형식)", example = "09:00")
        private String reminderTime;
    }

    /**
     * 전역 리마인더 설정 요청 DTO
     */
    @Schema(description = "전역 리마인더 설정 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReminderSettingsRequest {
        @Schema(description = "리마인더 활성화 여부", example = "true")
        private Boolean enabled;

        @Schema(description = "기본 알림 시점 (일 수)", example = "[1, 7, 30]")
        private java.util.List<Integer> defaultDaysBefore;

        @Schema(description = "알림 방법 (EMAIL, PUSH, BOTH)", example = "EMAIL")
        private String notificationMethod;
    }

    /**
     * 전역 리마인더 설정 응답 DTO
     */
    @Schema(description = "전역 리마인더 설정 응답")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReminderSettingsResponse {
        @Schema(description = "리마인더 활성화 여부", example = "true")
        private Boolean enabled;

        @Schema(description = "기본 알림 시점 (일 수)", example = "[1, 7, 30]")
        private java.util.List<Integer> defaultDaysBefore;

        @Schema(description = "알림 방법 (EMAIL, PUSH, BOTH)", example = "EMAIL")
        private String notificationMethod;
    }
}
