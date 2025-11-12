package com.daymemory.config;

import com.daymemory.interceptor.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitInterceptor rateLimitInterceptor;
    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 로깅 인터셉터 (모든 요청에 대해 로깅)
        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**")
                .order(1); // 가장 먼저 실행

        // Rate Limit 인터셉터
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/api/users/signup", "/api/users/login", "/api/users/refresh")
                .order(2); // 로깅 다음 실행
    }
}
