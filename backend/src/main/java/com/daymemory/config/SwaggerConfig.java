package com.daymemory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정
 * - Springdoc OpenAPI를 사용한 API 문서 자동 생성
 * - JWT 인증 설정
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정 Bean
     * - API 기본 정보 설정
     * - JWT Bearer 인증 스키마 설정
     */
    @Bean
    public OpenAPI openAPI() {
        // JWT 인증 스키마 이름
        String securitySchemeName = "bearerAuth";

        // API 기본 정보
        Info info = new Info()
                .title("Day Memory API")
                .version("1.0.0")
                .description("특별한 날을 기억하고 관리하는 Day Memory 애플리케이션의 REST API 문서")
                .contact(new Contact()
                        .name("Day Memory Team")
                        .email("support@daymemory.com"));

        // JWT Bearer 인증 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .name(securitySchemeName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description("JWT 토큰을 입력하세요. (Bearer 접두사 없이 토큰만 입력)");

        // Security Requirement 설정 (전역 적용)
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(securitySchemeName);

        return new OpenAPI()
                .info(info)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
