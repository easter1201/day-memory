package com.daymemory.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 관련 유틸리티 클래스
 */
public class SecurityUtils {

    private SecurityUtils() {
        // 유틸리티 클래스는 인스턴스화 방지
    }

    /**
     * 현재 인증된 사용자의 ID를 가져옵니다.
     *
     * @return 사용자 ID
     * @throws IllegalStateException 인증되지 않은 경우
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getId();
        }

        throw new IllegalStateException("인증 정보를 찾을 수 없습니다.");
    }

    /**
     * 현재 사용자가 인증되었는지 확인합니다.
     *
     * @return 인증 여부
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String);
    }
}
