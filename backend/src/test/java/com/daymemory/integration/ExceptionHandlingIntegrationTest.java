package com.daymemory.integration;

import com.daymemory.domain.dto.EventDto;
import com.daymemory.domain.dto.UserDto;
import com.daymemory.domain.entity.Event;
import com.daymemory.domain.entity.User;
import com.daymemory.domain.repository.UserRepository;
import com.daymemory.exception.CustomException;
import com.daymemory.exception.ErrorCode;
import com.daymemory.exception.GlobalExceptionHandler;
import com.daymemory.service.EventService;
import com.daymemory.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 예외 처리 통합 테스트
 * - GlobalExceptionHandler가 실제 애플리케이션 컨텍스트에서 정상 동작하는지 확인
 * - 커스텀 예외가 올바른 HTTP 상태 코드와 메시지로 변환되는지 확인
 * - Validation 예외가 올바르게 처리되는지 확인
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("예외 처리 통합 테스트")
class ExceptionHandlingIntegrationTest {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.builder()
                .email("exception.test@example.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("Exception Test User")
                .emailVerified(true)
                .build();
        testUser = userRepository.save(testUser);
    }

    // ===== CustomException 테스트 =====

    @Test
    @DisplayName("USER_NOT_FOUND 예외가 올바르게 처리됨")
    void testUserNotFoundException() {
        // Given: 존재하지 않는 사용자 ID
        Long invalidUserId = 99999L;

        // When & Then: USER_NOT_FOUND 예외 발생
        assertThatThrownBy(() -> {
            EventDto.Request request = EventDto.Request.builder()
                    .title("테스트 이벤트")
                    .recipientName("Test Person")
                    .relationship("Friend")
                    .eventDate(LocalDate.now().plusDays(30))
                    .eventType(Event.EventType.BIRTHDAY)
                    .build();
            eventService.createEvent(invalidUserId, request);
        })
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("USER_NOT_FOUND CustomException이 올바른 응답으로 변환됨")
    void testUserNotFoundExceptionHandler() {
        // Given: USER_NOT_FOUND 예외
        CustomException exception = new CustomException(ErrorCode.USER_NOT_FOUND);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 올바른 응답 생성
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(404);
    }

    @Test
    @DisplayName("EVENT_NOT_FOUND 예외가 올바르게 처리됨")
    void testEventNotFoundException() {
        // Given: 존재하지 않는 이벤트 ID
        Long invalidEventId = 99999L;

        // When & Then: EVENT_NOT_FOUND 예외 발생
        assertThatThrownBy(() -> eventService.getEvent(invalidEventId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.EVENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("EVENT_NOT_FOUND CustomException이 올바른 응답으로 변환됨")
    void testEventNotFoundExceptionHandler() {
        // Given: EVENT_NOT_FOUND 예외
        CustomException exception = new CustomException(ErrorCode.EVENT_NOT_FOUND);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 올바른 응답 생성
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("EVENT_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.EVENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("USER_ALREADY_EXISTS 예외가 올바르게 처리됨")
    void testUserAlreadyExistsException() {
        // Given: 이미 존재하는 이메일
        UserDto.SignupRequest firstRequest = UserDto.SignupRequest.builder()
                .email("duplicate@example.com")
                .password("password123")
                .nickname("First User")
                .build();
        userService.signup(firstRequest);

        // When & Then: USER_ALREADY_EXISTS 예외 발생
        UserDto.SignupRequest secondRequest = UserDto.SignupRequest.builder()
                .email("duplicate@example.com")
                .password("password456")
                .nickname("Second User")
                .build();

        assertThatThrownBy(() -> userService.signup(secondRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.USER_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("USER_ALREADY_EXISTS CustomException이 올바른 응답으로 변환됨")
    void testUserAlreadyExistsExceptionHandler() {
        // Given: USER_ALREADY_EXISTS 예외
        CustomException exception = new CustomException(ErrorCode.USER_ALREADY_EXISTS);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 올바른 응답 생성 (409 CONFLICT)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_ALREADY_EXISTS");
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.USER_ALREADY_EXISTS.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("INVALID_PASSWORD 예외가 올바르게 처리됨")
    void testInvalidPasswordException() {
        // Given: 사용자 생성
        UserDto.SignupRequest signupRequest = UserDto.SignupRequest.builder()
                .email("password.test@example.com")
                .password("correctPassword")
                .nickname("Password Test User")
                .build();
        userService.signup(signupRequest);

        // When & Then: 잘못된 비밀번호로 로그인 시도
        UserDto.LoginRequest loginRequest = UserDto.LoginRequest.builder()
                .email("password.test@example.com")
                .password("wrongPassword")
                .build();

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("INVALID_PASSWORD CustomException이 올바른 응답으로 변환됨")
    void testInvalidPasswordExceptionHandler() {
        // Given: INVALID_PASSWORD 예외
        CustomException exception = new CustomException(ErrorCode.INVALID_PASSWORD);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 올바른 응답 생성 (401 UNAUTHORIZED)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("INVALID_PASSWORD");
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.INVALID_PASSWORD.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("커스텀 메시지를 가진 CustomException이 올바르게 처리됨")
    void testCustomExceptionWithCustomMessage() {
        // Given: 커스텀 메시지를 가진 예외
        String customMessage = "이것은 커스텀 메시지입니다";
        CustomException exception = new CustomException(ErrorCode.USER_NOT_FOUND, customMessage);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 기본 에러 코드 정보가 사용됨 (커스텀 메시지는 로그에만 기록)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("USER_NOT_FOUND");
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    // ===== Validation 예외 테스트 =====

    @Test
    @DisplayName("MethodArgumentNotValidException이 올바르게 처리됨")
    void testValidationExceptionHandler() {
        // Given: Validation 예외 생성 (실제 유효성 검증 실패를 시뮬레이션)
        EventDto.Request request = new EventDto.Request();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "request");

        bindingResult.addError(new FieldError("request", "title", null, false,
                null, null, "제목은 필수입니다"));
        bindingResult.addError(new FieldError("request", "eventDate", null, false,
                null, null, "이벤트 날짜는 필수입니다"));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response =
                globalExceptionHandler.handleValidationException(exception);

        // Then: 올바른 Validation 에러 응답 생성
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.VALIDATION_ERROR.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getErrors()).hasSize(2);
    }

    @Test
    @DisplayName("Validation 에러 필드 정보가 올바르게 포함됨")
    void testValidationErrorDetails() {
        // Given: Validation 예외 생성
        EventDto.Request request = new EventDto.Request();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "request");

        bindingResult.addError(new FieldError("request", "title", "invalidValue", false,
                null, null, "제목은 필수입니다"));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response =
                globalExceptionHandler.handleValidationException(exception);

        // Then: 필드 에러 상세 정보 확인
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors()).hasSize(1);

        GlobalExceptionHandler.FieldErrorDetail errorDetail = response.getBody().getErrors().get(0);
        assertThat(errorDetail.getField()).isEqualTo("title");
        assertThat(errorDetail.getRejectedValue()).isEqualTo("invalidValue");
        assertThat(errorDetail.getMessage()).isEqualTo("제목은 필수입니다");
    }

    // ===== 일반 예외 테스트 =====

    @Test
    @DisplayName("예상하지 못한 Exception이 500 에러로 처리됨")
    void testGenericExceptionHandler() {
        // Given: 일반 예외
        Exception exception = new RuntimeException("예상하지 못한 오류");

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleException(exception);

        // Then: 500 INTERNAL_SERVER_ERROR 응답 생성
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("SERVER_INTERNAL_ERROR");
        assertThat(response.getBody().getMessage()).isEqualTo(ErrorCode.SERVER_INTERNAL_ERROR.getMessage());
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    // ===== 여러 ErrorCode 테스트 =====

    @Test
    @DisplayName("UNAUTHORIZED 예외가 401 상태 코드로 처리됨")
    void testUnauthorizedException() {
        // Given: UNAUTHORIZED 예외
        CustomException exception = new CustomException(ErrorCode.UNAUTHORIZED);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 401 UNAUTHORIZED 응답
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo("UNAUTHORIZED");
    }

    @Test
    @DisplayName("FORBIDDEN 예외가 403 상태 코드로 처리됨")
    void testForbiddenException() {
        // Given: FORBIDDEN 예외
        CustomException exception = new CustomException(ErrorCode.FORBIDDEN);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 403 FORBIDDEN 응답
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getCode()).isEqualTo("FORBIDDEN");
    }

    @Test
    @DisplayName("BAD_REQUEST 예외가 400 상태 코드로 처리됨")
    void testBadRequestException() {
        // Given: INVALID_REQUEST 예외
        CustomException exception = new CustomException(ErrorCode.INVALID_REQUEST);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 400 BAD_REQUEST 응답
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo("INVALID_REQUEST");
    }

    @Test
    @DisplayName("GIFT_NOT_FOUND 예외가 404 상태 코드로 처리됨")
    void testGiftNotFoundException() {
        // Given: GIFT_NOT_FOUND 예외
        CustomException exception = new CustomException(ErrorCode.GIFT_NOT_FOUND);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 404 NOT_FOUND 응답
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("GIFT_NOT_FOUND");
    }

    @Test
    @DisplayName("REMINDER_NOT_FOUND 예외가 404 상태 코드로 처리됨")
    void testReminderNotFoundException() {
        // Given: REMINDER_NOT_FOUND 예외
        CustomException exception = new CustomException(ErrorCode.REMINDER_NOT_FOUND);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 404 NOT_FOUND 응답
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("REMINDER_NOT_FOUND");
    }

    @Test
    @DisplayName("여러 CustomException이 각각 올바른 HTTP 상태 코드로 매핑됨")
    void testMultipleExceptionStatusCodes() {
        // 404 예외들
        assertThat(new CustomException(ErrorCode.USER_NOT_FOUND).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(new CustomException(ErrorCode.EVENT_NOT_FOUND).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(new CustomException(ErrorCode.GIFT_NOT_FOUND).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND);

        // 400 예외들
        assertThat(new CustomException(ErrorCode.INVALID_REQUEST).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(new CustomException(ErrorCode.VALIDATION_ERROR).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        // 401 예외들
        assertThat(new CustomException(ErrorCode.UNAUTHORIZED).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(new CustomException(ErrorCode.INVALID_PASSWORD).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);

        // 409 예외
        assertThat(new CustomException(ErrorCode.USER_ALREADY_EXISTS).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.CONFLICT);

        // 500 예외들
        assertThat(new CustomException(ErrorCode.SERVER_INTERNAL_ERROR).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(new CustomException(ErrorCode.DATABASE_ERROR).getErrorCode().getStatus())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("GlobalExceptionHandler 빈이 정상적으로 주입됨")
    void testGlobalExceptionHandlerBeanInjection() {
        assertThat(globalExceptionHandler).isNotNull();
    }

    @Test
    @DisplayName("에러 응답에 타임스탬프가 포함됨")
    void testErrorResponseContainsTimestamp() {
        // Given: CustomException
        CustomException exception = new CustomException(ErrorCode.USER_NOT_FOUND);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                globalExceptionHandler.handleCustomException(exception);

        // Then: 타임스탬프가 포함됨
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Validation 에러 응답에 타임스탬프가 포함됨")
    void testValidationErrorResponseContainsTimestamp() {
        // Given: Validation 예외
        EventDto.Request request = new EventDto.Request();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        bindingResult.addError(new FieldError("request", "title", "제목은 필수입니다"));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(null, bindingResult);

        // When: GlobalExceptionHandler로 처리
        ResponseEntity<GlobalExceptionHandler.ValidationErrorResponse> response =
                globalExceptionHandler.handleValidationException(exception);

        // Then: 타임스탬프가 포함됨
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
