package com.daymemory.integration;

import com.daymemory.config.CorsConfig;
import com.daymemory.config.SecurityConfig;
import com.daymemory.config.SwaggerConfig;
import com.daymemory.config.WebConfig;
import com.daymemory.controller.*;
import com.daymemory.domain.repository.*;
import com.daymemory.security.CustomUserDetailsService;
import com.daymemory.security.JwtTokenProvider;
import com.daymemory.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 애플리케이션 컨텍스트 통합 테스트
 * - 애플리케이션이 정상적으로 로드되는지 확인
 * - 모든 주요 빈이 정상적으로 주입되는지 확인
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("애플리케이션 컨텍스트 통합 테스트")
class ApplicationContextTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("애플리케이션 컨텍스트 로드 성공")
    void testContextLoads() {
        assertThat(context).isNotNull();
    }

    // ===== Controller Bean 테스트 =====

    @Test
    @DisplayName("모든 Controller 빈 주입 확인")
    void testControllerBeansInjection() {
        // 주요 컨트롤러 빈 확인
        assertThat(context.getBean(EventController.class)).isNotNull();
        assertThat(context.getBean(UserController.class)).isNotNull();
        assertThat(context.getBean(GiftItemController.class)).isNotNull();
        assertThat(context.getBean(ReminderController.class)).isNotNull();
        assertThat(context.getBean(DashboardController.class)).isNotNull();
        assertThat(context.getBean(StatisticsController.class)).isNotNull();
        assertThat(context.getBean(AIRecommendationController.class)).isNotNull();
        assertThat(context.getBean(OAuthController.class)).isNotNull();
        assertThat(context.getBean(VerificationController.class)).isNotNull();
    }

    @Test
    @DisplayName("EventController 빈 주입 확인")
    void testEventControllerInjection() {
        EventController controller = context.getBean(EventController.class);
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("UserController 빈 주입 확인")
    void testUserControllerInjection() {
        UserController controller = context.getBean(UserController.class);
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("GiftItemController 빈 주입 확인")
    void testGiftItemControllerInjection() {
        GiftItemController controller = context.getBean(GiftItemController.class);
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("ReminderController 빈 주입 확인")
    void testReminderControllerInjection() {
        ReminderController controller = context.getBean(ReminderController.class);
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("AIRecommendationController 빈 주입 확인")
    void testAIRecommendationControllerInjection() {
        AIRecommendationController controller = context.getBean(AIRecommendationController.class);
        assertThat(controller).isNotNull();
    }

    // ===== Service Bean 테스트 =====

    @Test
    @DisplayName("모든 Service 빈 주입 확인")
    void testServiceBeansInjection() {
        // 주요 서비스 빈 확인
        assertThat(context.getBean(EventService.class)).isNotNull();
        assertThat(context.getBean(UserService.class)).isNotNull();
        assertThat(context.getBean(GiftItemService.class)).isNotNull();
        assertThat(context.getBean(ReminderService.class)).isNotNull();
        assertThat(context.getBean(EmailService.class)).isNotNull();
        assertThat(context.getBean(DashboardService.class)).isNotNull();
        assertThat(context.getBean(StatisticsService.class)).isNotNull();
        assertThat(context.getBean(AIRecommendationService.class)).isNotNull();
        assertThat(context.getBean(OAuthService.class)).isNotNull();
        assertThat(context.getBean(VerificationService.class)).isNotNull();
        assertThat(context.getBean(RecurringEventService.class)).isNotNull();
        assertThat(context.getBean(FileStorageService.class)).isNotNull();
    }

    @Test
    @DisplayName("EventService 빈 주입 확인")
    void testEventServiceInjection() {
        EventService service = context.getBean(EventService.class);
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("UserService 빈 주입 확인")
    void testUserServiceInjection() {
        UserService service = context.getBean(UserService.class);
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("GiftItemService 빈 주입 확인")
    void testGiftItemServiceInjection() {
        GiftItemService service = context.getBean(GiftItemService.class);
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("ReminderService 빈 주입 확인")
    void testReminderServiceInjection() {
        ReminderService service = context.getBean(ReminderService.class);
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("EmailService 빈 주입 확인")
    void testEmailServiceInjection() {
        EmailService service = context.getBean(EmailService.class);
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("AIRecommendationService 빈 주입 확인")
    void testAIRecommendationServiceInjection() {
        AIRecommendationService service = context.getBean(AIRecommendationService.class);
        assertThat(service).isNotNull();
    }

    // ===== Repository Bean 테스트 =====

    @Test
    @DisplayName("모든 Repository 빈 주입 확인")
    void testRepositoryBeansInjection() {
        // 주요 리포지토리 빈 확인
        assertThat(context.getBean(EventRepository.class)).isNotNull();
        assertThat(context.getBean(UserRepository.class)).isNotNull();
        assertThat(context.getBean(GiftItemRepository.class)).isNotNull();
        assertThat(context.getBean(EventReminderRepository.class)).isNotNull();
        assertThat(context.getBean(ReminderLogRepository.class)).isNotNull();
        assertThat(context.getBean(VerificationTokenRepository.class)).isNotNull();
    }

    @Test
    @DisplayName("EventRepository 빈 주입 확인")
    void testEventRepositoryInjection() {
        EventRepository repository = context.getBean(EventRepository.class);
        assertThat(repository).isNotNull();
    }

    @Test
    @DisplayName("UserRepository 빈 주입 확인")
    void testUserRepositoryInjection() {
        UserRepository repository = context.getBean(UserRepository.class);
        assertThat(repository).isNotNull();
    }

    @Test
    @DisplayName("GiftItemRepository 빈 주입 확인")
    void testGiftItemRepositoryInjection() {
        GiftItemRepository repository = context.getBean(GiftItemRepository.class);
        assertThat(repository).isNotNull();
    }

    @Test
    @DisplayName("EventReminderRepository 빈 주입 확인")
    void testEventReminderRepositoryInjection() {
        EventReminderRepository repository = context.getBean(EventReminderRepository.class);
        assertThat(repository).isNotNull();
    }

    @Test
    @DisplayName("ReminderLogRepository 빈 주입 확인")
    void testReminderLogRepositoryInjection() {
        ReminderLogRepository repository = context.getBean(ReminderLogRepository.class);
        assertThat(repository).isNotNull();
    }

    // ===== Security Bean 테스트 =====

    @Test
    @DisplayName("모든 Security 빈 주입 확인")
    void testSecurityBeansInjection() {
        // 보안 관련 빈 확인
        assertThat(context.getBean(JwtTokenProvider.class)).isNotNull();
        assertThat(context.getBean(CustomUserDetailsService.class)).isNotNull();
        assertThat(context.getBean(SecurityConfig.class)).isNotNull();
        assertThat(context.getBean(PasswordEncoder.class)).isNotNull();
    }

    @Test
    @DisplayName("JwtTokenProvider 빈 주입 확인")
    void testJwtTokenProviderInjection() {
        JwtTokenProvider jwtTokenProvider = context.getBean(JwtTokenProvider.class);
        assertThat(jwtTokenProvider).isNotNull();
    }

    @Test
    @DisplayName("CustomUserDetailsService 빈 주입 확인")
    void testCustomUserDetailsServiceInjection() {
        CustomUserDetailsService service = context.getBean(CustomUserDetailsService.class);
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("PasswordEncoder 빈 주입 확인")
    void testPasswordEncoderInjection() {
        PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
        assertThat(encoder).isNotNull();
    }

    // ===== Configuration Bean 테스트 =====

    @Test
    @DisplayName("모든 Configuration 빈 주입 확인")
    void testConfigurationBeansInjection() {
        // 설정 빈 확인
        assertThat(context.getBean(SecurityConfig.class)).isNotNull();
        assertThat(context.getBean(CorsConfig.class)).isNotNull();
        assertThat(context.getBean(WebConfig.class)).isNotNull();
        assertThat(context.getBean(SwaggerConfig.class)).isNotNull();
    }

    @Test
    @DisplayName("SecurityConfig 빈 주입 확인")
    void testSecurityConfigInjection() {
        SecurityConfig config = context.getBean(SecurityConfig.class);
        assertThat(config).isNotNull();
    }

    @Test
    @DisplayName("CorsConfig 빈 주입 확인")
    void testCorsConfigInjection() {
        CorsConfig config = context.getBean(CorsConfig.class);
        assertThat(config).isNotNull();
    }

    @Test
    @DisplayName("WebConfig 빈 주입 확인")
    void testWebConfigInjection() {
        WebConfig config = context.getBean(WebConfig.class);
        assertThat(config).isNotNull();
    }

    @Test
    @DisplayName("SwaggerConfig 빈 주입 확인")
    void testSwaggerConfigInjection() {
        SwaggerConfig config = context.getBean(SwaggerConfig.class);
        assertThat(config).isNotNull();
    }

    // ===== 통합 테스트 =====

    @Test
    @DisplayName("모든 핵심 빈이 정상적으로 주입되었는지 통합 확인")
    void testAllCriticalBeansInjection() {
        // Controllers
        assertThat(context.getBean(EventController.class)).isNotNull();
        assertThat(context.getBean(UserController.class)).isNotNull();
        assertThat(context.getBean(GiftItemController.class)).isNotNull();

        // Services
        assertThat(context.getBean(EventService.class)).isNotNull();
        assertThat(context.getBean(UserService.class)).isNotNull();
        assertThat(context.getBean(GiftItemService.class)).isNotNull();

        // Repositories
        assertThat(context.getBean(EventRepository.class)).isNotNull();
        assertThat(context.getBean(UserRepository.class)).isNotNull();
        assertThat(context.getBean(GiftItemRepository.class)).isNotNull();

        // Security
        assertThat(context.getBean(JwtTokenProvider.class)).isNotNull();
        assertThat(context.getBean(CustomUserDetailsService.class)).isNotNull();

        // Configurations
        assertThat(context.getBean(SecurityConfig.class)).isNotNull();
        assertThat(context.getBean(CorsConfig.class)).isNotNull();
    }
}
