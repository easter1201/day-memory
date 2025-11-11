package com.daymemory.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * HTTP 요청/응답 로깅 인터셉터
 * - 요청 정보 (메서드, URI, 파라미터) 로깅
 * - 응답 정보 (상태 코드, 처리 시간) 로깅
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String REQUEST_ID = "requestId";

    /**
     * 컨트롤러 실행 전
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청 시작 시간 기록
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());

        // 요청 ID 생성
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute(REQUEST_ID, requestId);

        // 요청 로그
        log.info("[{}] {} {} - Remote IP: {}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                getClientIp(request));

        // 쿼리 스트링 로깅
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            log.debug("[{}] Query Parameters: {}", requestId, queryString);
        }

        return true;
    }

    /**
     * 컨트롤러 실행 후, 뷰 렌더링 전
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        // 뷰가 있는 경우 (일반적으로 REST API는 없음)
        if (modelAndView != null) {
            String requestId = (String) request.getAttribute(REQUEST_ID);
            log.debug("[{}] ModelAndView: {}", requestId, modelAndView.getViewName());
        }
    }

    /**
     * 요청 완료 후 (뷰 렌더링 후)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        String requestId = (String) request.getAttribute(REQUEST_ID);

        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;

            // 응답 상태 코드에 따른 로그 레벨 분류
            int status = response.getStatus();
            if (status >= 500) {
                // 5xx 에러: ERROR 로그
                log.error("[{}] {} {} - Status: {}, Time: {}ms",
                        requestId,
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        executionTime);
            } else if (status >= 400) {
                // 4xx 에러: WARN 로그
                log.warn("[{}] {} {} - Status: {}, Time: {}ms",
                        requestId,
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        executionTime);
            } else {
                // 정상 응답: INFO 로그
                log.info("[{}] {} {} - Status: {}, Time: {}ms",
                        requestId,
                        request.getMethod(),
                        request.getRequestURI(),
                        status,
                        executionTime);
            }

            // 느린 요청 경고 (1초 이상)
            if (executionTime > 1000) {
                log.warn("[{}] Slow Request Detected - {} {} took {}ms",
                        requestId,
                        request.getMethod(),
                        request.getRequestURI(),
                        executionTime);
            }
        }

        // 예외 로깅
        if (ex != null) {
            log.error("[{}] Request Exception: {} - {}",
                    requestId,
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    ex);
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     * - X-Forwarded-For, X-Real-IP 헤더 확인
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For에 여러 IP가 있는 경우 첫 번째 IP 추출
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
