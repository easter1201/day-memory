package com.daymemory.interceptor;

import com.daymemory.config.RateLimitConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, RateLimitConfig.RateLimitEntry> rateLimitStore;

    private static final int MAX_REQUESTS_PER_MINUTE = 100; // 분당 최대 요청 수

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);
        String key = clientIp + ":" + request.getRequestURI();

        RateLimitConfig.RateLimitEntry entry = rateLimitStore.get(key);

        if (entry == null) {
            // 첫 요청
            rateLimitStore.put(key, new RateLimitConfig.RateLimitEntry());
            return true;
        }

        if (entry.isExpired()) {
            // 만료된 엔트리 갱신
            rateLimitStore.put(key, new RateLimitConfig.RateLimitEntry());
            return true;
        }

        if (entry.getCount() >= MAX_REQUESTS_PER_MINUTE) {
            // Rate Limit 초과
            log.warn("Rate limit exceeded for IP: {} on URI: {}", clientIp, request.getRequestURI());
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요.\"}");
            return false;
        }

        // 요청 카운트 증가
        entry.increment();
        return true;
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
