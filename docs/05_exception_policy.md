# 예외 처리 정책 (05_exception_policy.md)

## 📌 개요

Day Memory 프로젝트의 예외 처리 정책과 에러 코드 체계를 정의합니다.
일관된 에러 응답 형식, HTTP 상태 코드 매핑, 도메인별 코드 접두사를 지정합니다.

---

## 1. 예외 처리 기본 원칙

### 1.1 예외 계층 구조

```
Throwable
  └── Exception
      ├── RuntimeException
      │   └── CustomException (프로젝트 커스텀 예외)
      │       ├── BusinessException (비즈니스 로직 에러)
      │       ├── AuthenticationException (인증 에러)
      │       └── ValidationException (입력값 검증 에러)
      └── Checked Exceptions (외부 API 호출 등)
```

### 1.2 처리 방식

- **RuntimeException 계열**: 비즈니스 로직 에러, 400/404 응답
- **Checked Exception**: 외부 연동 실패, 500 응답
- **Spring Validation**: @Valid 검증 실패, 400 응답

---

## 2. 에러 응답 형식

### 2.1 표준 에러 응답 (JSON)

```json
{
  "status": 404,
  "code": "EVENT_NOT_FOUND",
  "message": "해당 이벤트를 찾을 수 없습니다.",
  "timestamp": "2025-01-10T15:30:45"
}
```

### 2.2 필드별 설명

| 필드        | 타입       | 설명                                    |
|-----------|----------|---------------------------------------|
| status    | Integer  | HTTP 상태 코드 (400, 404, 500 등)         |
| code      | String   | 에러 코드 (도메인_상황 형식, 예: USER_NOT_FOUND) |
| message   | String   | 사용자 친화적인 에러 메시지                      |
| timestamp | String   | ISO 8601 형식 타임스탬프                    |

### 2.3 Validation 에러 응답

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "입력값 검증에 실패했습니다.",
  "errors": [
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "이메일 형식이 올바르지 않습니다."
    },
    {
      "field": "password",
      "rejectedValue": null,
      "message": "비밀번호는 8자 이상이어야 합니다."
    }
  ],
  "timestamp": "2025-01-10T15:30:45"
}
```

---

## 3. 에러 코드 체계

### 3.1 도메인별 코드 접두사

| 도메인        | 접두사   | 예시                        |
|------------|-------|---------------------------|
| 사용자 인증     | USER_ | USER_NOT_FOUND            |
| 이벤트/기념일   | EVENT_ | EVENT_NOT_FOUND           |
| 리마인더      | REMINDER_ | REMINDER_ALREADY_SENT     |
| 선물         | GIFT_ | GIFT_NOT_FOUND            |
| 공통 (입력 검증) | VALIDATION_ | VALIDATION_ERROR          |
| 공통 (서버)    | SERVER_ | SERVER_INTERNAL_ERROR     |

### 3.2 HTTP 상태 코드 매핑

| HTTP Status | 사용 상황                    | 예시 코드                  |
|-------------|--------------------------|------------------------|
| 400         | 잘못된 요청, 입력값 검증 실패       | VALIDATION_ERROR       |
| 401         | 인증 실패 (로그인 필요)           | UNAUTHORIZED           |
| 403         | 권한 없음 (접근 거부)            | FORBIDDEN              |
| 404         | 리소스를 찾을 수 없음             | USER_NOT_FOUND         |
| 409         | 중복 데이터 (이메일 중복 등)       | USER_ALREADY_EXISTS    |
| 500         | 서버 내부 오류                 | SERVER_INTERNAL_ERROR  |
| 503         | 외부 서비스 연동 실패             | EXTERNAL_SERVICE_ERROR |

---

## 4. 에러 코드 목록

### 4.1 사용자 인증 (USER_)

| 에러 코드                 | HTTP Status | 메시지                      | 발생 상황              |
|------------------------|-------------|--------------------------|---------------------|
| USER_NOT_FOUND         | 404         | 존재하지 않는 사용자입니다.         | userId로 조회 실패       |
| USER_ALREADY_EXISTS    | 409         | 이미 존재하는 이메일입니다.         | 회원가입 시 이메일 중복      |
| INVALID_PASSWORD       | 401         | 비밀번호가 올바르지 않습니다.        | 로그인 시 비밀번호 불일치     |
| INVALID_TOKEN          | 401         | 유효하지 않은 토큰입니다.          | JWT 검증 실패          |
| EXPIRED_TOKEN          | 401         | 만료된 토큰입니다.              | JWT 만료              |
| UNAUTHORIZED           | 401         | 인증이 필요합니다.              | 토큰 없이 API 호출       |
| FORBIDDEN              | 403         | 접근 권한이 없습니다.            | 다른 사용자의 리소스 접근 시도  |

### 4.2 이벤트/기념일 (EVENT_)

| 에러 코드                      | HTTP Status | 메시지                          | 발생 상황                   |
|----------------------------|-------------|------------------------------|-----------------------------|
| EVENT_NOT_FOUND            | 404         | 해당 이벤트를 찾을 수 없습니다.         | eventId로 조회 실패            |
| EVENT_DATE_INVALID         | 400         | 유효하지 않은 이벤트 날짜입니다.         | 과거 날짜 또는 형식 오류           |
| EVENT_TYPE_INVALID         | 400         | 유효하지 않은 이벤트 타입입니다.         | EventType enum에 없는 값      |
| EVENT_ACCESS_DENIED        | 403         | 해당 이벤트에 접근할 권한이 없습니다.     | 다른 사용자의 이벤트 수정/삭제 시도    |
| REMINDER_DAYS_INVALID      | 400         | 리마인더 일수는 1 이상이어야 합니다.     | reminderDays에 0 또는 음수 포함  |

### 4.3 리마인더 (REMINDER_)

| 에러 코드                      | HTTP Status | 메시지                          | 발생 상황                   |
|----------------------------|-------------|------------------------------|-----------------------------|
| REMINDER_NOT_FOUND         | 404         | 리마인더를 찾을 수 없습니다.           | reminderId로 조회 실패         |
| REMINDER_ALREADY_SENT      | 409         | 이미 발송된 리마인더입니다.            | 24시간 이내 중복 발송 시도         |
| EMAIL_SEND_FAILED          | 500         | 이메일 발송에 실패했습니다.            | SMTP 서버 오류                |

### 4.4 선물 (GIFT_)

| 에러 코드                      | HTTP Status | 메시지                          | 발생 상황                   |
|----------------------------|-------------|------------------------------|-----------------------------|
| GIFT_NOT_FOUND             | 404         | 선물을 찾을 수 없습니다.             | giftId로 조회 실패             |
| GIFT_ACCESS_DENIED         | 403         | 해당 선물에 접근할 권한이 없습니다.      | 다른 사용자의 선물 수정/삭제 시도     |
| GIFT_CATEGORY_INVALID      | 400         | 유효하지 않은 선물 카테고리입니다.       | GiftCategory enum에 없는 값   |

### 4.5 AI 추천 (AI_)

| 에러 코드                      | HTTP Status | 메시지                          | 발생 상황                   |
|----------------------------|-------------|------------------------------|-----------------------------|
| AI_SERVICE_UNAVAILABLE     | 503         | AI 서비스를 사용할 수 없습니다.        | OpenAI/Claude API 연동 실패   |
| AI_REQUEST_FAILED          | 500         | AI 추천 요청에 실패했습니다.         | API 호출 타임아웃 또는 오류        |

### 4.6 공통 (VALIDATION_, SERVER_)

| 에러 코드                      | HTTP Status | 메시지                          | 발생 상황                   |
|----------------------------|-------------|------------------------------|-----------------------------|
| VALIDATION_ERROR           | 400         | 입력값 검증에 실패했습니다.            | @Valid 검증 실패             |
| INVALID_REQUEST            | 400         | 잘못된 요청입니다.                  | 필수 파라미터 누락 또는 형식 오류      |
| SERVER_INTERNAL_ERROR      | 500         | 서버 내부 오류가 발생했습니다.         | 예상하지 못한 예외 발생            |
| DATABASE_ERROR             | 500         | 데이터베이스 오류가 발생했습니다.       | DB 연결 실패, 쿼리 오류          |

---

## 5. CustomException 클래스 설계

### 5.1 ErrorCode enum

```java
package com.daymemory.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // Event
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."),
    EVENT_DATE_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 이벤트 날짜입니다."),
    EVENT_TYPE_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 이벤트 타입입니다."),
    EVENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 이벤트에 접근할 권한이 없습니다."),
    REMINDER_DAYS_INVALID(HttpStatus.BAD_REQUEST, "리마인더 일수는 1 이상이어야 합니다."),

    // Reminder
    REMINDER_NOT_FOUND(HttpStatus.NOT_FOUND, "리마인더를 찾을 수 없습니다."),
    REMINDER_ALREADY_SENT(HttpStatus.CONFLICT, "이미 발송된 리마인더입니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),

    // Gift
    GIFT_NOT_FOUND(HttpStatus.NOT_FOUND, "선물을 찾을 수 없습니다."),
    GIFT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 선물에 접근할 권한이 없습니다."),
    GIFT_CATEGORY_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 선물 카테고리입니다."),

    // AI
    AI_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "AI 서비스를 사용할 수 없습니다."),
    AI_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI 추천 요청에 실패했습니다."),

    // Common
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    SERVER_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
```

### 5.2 CustomException 클래스

```java
package com.daymemory.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
```

---

## 6. GlobalExceptionHandler 구현

### 6.1 전역 예외 핸들러

```java
package com.daymemory.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(response);
    }

    // @Valid 검증 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
        MethodArgumentNotValidException e
    ) {
        List<FieldErrorDetail> errors = e.getBindingResult().getFieldErrors().stream()
            .map(FieldErrorDetail::of)
            .collect(Collectors.toList());

        ValidationErrorResponse response = ValidationErrorResponse.of(
            ErrorCode.VALIDATION_ERROR,
            errors
        );

        return ResponseEntity
            .status(ErrorCode.VALIDATION_ERROR.getStatus())
            .body(response);
    }

    // 예상하지 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = ErrorResponse.of(ErrorCode.SERVER_INTERNAL_ERROR);
        return ResponseEntity
            .status(ErrorCode.SERVER_INTERNAL_ERROR.getStatus())
            .body(response);
    }
}
```

### 6.2 ErrorResponse DTO

```java
package com.daymemory.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private int status;
    private String code;
    private String message;
    private LocalDateTime timestamp;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .status(errorCode.getStatus().value())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
    }
}
```

### 6.3 ValidationErrorResponse DTO

```java
package com.daymemory.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ValidationErrorResponse {
    private int status;
    private String code;
    private String message;
    private List<FieldErrorDetail> errors;
    private LocalDateTime timestamp;

    public static ValidationErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> errors) {
        return ValidationErrorResponse.builder()
            .status(errorCode.getStatus().value())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .errors(errors)
            .timestamp(LocalDateTime.now())
            .build();
    }
}

@Getter
@AllArgsConstructor
class FieldErrorDetail {
    private String field;
    private Object rejectedValue;
    private String message;

    public static FieldErrorDetail of(FieldError fieldError) {
        return new FieldErrorDetail(
            fieldError.getField(),
            fieldError.getRejectedValue(),
            fieldError.getDefaultMessage()
        );
    }
}
```

---

## 7. Service에서 예외 발생 예시

### 7.1 EventService

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventDto.Response getEvent(Long eventId, Long userId) {
        Event event = eventRepository.findByIdWithUserAndReminders(eventId)
            .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // 권한 확인
        if (!event.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.EVENT_ACCESS_DENIED);
        }

        return EventDto.Response.from(event);
    }

    @Transactional
    public EventDto.Response createEvent(EventDto.Request request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // reminderDays 검증
        if (request.getReminderDays().stream().anyMatch(day -> day < 1)) {
            throw new CustomException(ErrorCode.REMINDER_DAYS_INVALID);
        }

        // 이벤트 생성 로직...
    }
}
```

### 7.2 UserService (인증)

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto.LoginResponse login(UserDto.LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // JWT 생성 로직...
    }

    public void signup(UserDto.SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // 회원가입 로직...
    }
}
```

---

## 8. 예외 처리 흐름도

```
Client 요청
    ↓
Controller (DTO 검증: @Valid)
    ↓
┌──────────────────┐
│ @Valid 실패?      │ → YES → MethodArgumentNotValidException
│ (예: 필수값 누락)   │         ↓
└──────────────────┘    GlobalExceptionHandler.handleValidationException()
    NO                      ↓
    ↓                  ValidationErrorResponse (400)
Service (비즈니스 로직)
    ↓
┌──────────────────┐
│ CustomException  │ → YES → GlobalExceptionHandler.handleCustomException()
│ 발생?             │         ↓
└──────────────────┘    ErrorResponse (400/404/500)
    NO
    ↓
정상 응답 (200 OK)
```

---

## 9. DTO Validation 규칙

### 9.1 공통 검증 어노테이션

| 어노테이션             | 적용 대상    | 메시지 예시                 |
|-------------------|----------|-------------------------|
| @NotNull          | 모든 필수 필드 | "필수 입력 항목입니다."          |
| @NotBlank         | String   | "공백일 수 없습니다."          |
| @Email            | email    | "이메일 형식이 올바르지 않습니다."   |
| @Size(min, max)   | String   | "8~20자 이내로 입력해주세요."    |
| @Past             | LocalDate | "과거 날짜만 입력 가능합니다."     |
| @Future           | LocalDate | "미래 날짜만 입력 가능합니다."     |
| @Positive         | Integer  | "양수만 입력 가능합니다."        |

### 9.2 EventDto.Request 예시

```java
@Getter
@Builder
public static class Request {
    @NotBlank(message = "이벤트 제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내로 입력해주세요.")
    private String title;

    @NotNull(message = "이벤트 날짜는 필수입니다.")
    private LocalDate eventDate;

    @NotNull(message = "이벤트 타입은 필수입니다.")
    private Event.EventType eventType;

    private List<@Positive(message = "리마인더 일수는 양수여야 합니다.") Integer> reminderDays;

    private Boolean isTracking;
}
```

---

## 10. 프론트엔드 연동 가이드

### 10.1 에러 처리 예시 (React + Redux)

```javascript
// API 호출 예시
try {
  const response = await fetch('/api/events', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(eventData)
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }

  const data = await response.json();
  return data;

} catch (error) {
  // error.message = "해당 이벤트를 찾을 수 없습니다."
  alert(error.message);
}
```

### 10.2 에러 코드별 처리

```javascript
const handleApiError = (error) => {
  switch (error.code) {
    case 'INVALID_TOKEN':
    case 'EXPIRED_TOKEN':
      // 토큰 재발급 또는 로그인 페이지로 이동
      refreshToken();
      break;
    case 'FORBIDDEN':
    case 'EVENT_ACCESS_DENIED':
      // 권한 없음 알림
      showAlert('접근 권한이 없습니다.');
      break;
    case 'VALIDATION_ERROR':
      // 입력값 검증 실패 표시
      error.errors.forEach(fieldError => {
        showFieldError(fieldError.field, fieldError.message);
      });
      break;
    default:
      // 기본 에러 메시지 표시
      showAlert(error.message);
  }
};
```

---

## 11. 로깅 전략

### 11.1 로그 레벨 정책

| 로그 레벨   | 사용 상황                     | 예시                          |
|---------|---------------------------|------------------------------|
| ERROR   | 500 에러, 외부 서비스 연동 실패     | EMAIL_SEND_FAILED            |
| WARN    | 비즈니스 로직 위반 (400, 404)    | EVENT_NOT_FOUND              |
| INFO    | API 호출 로그                 | "GET /api/events - 200 OK"   |
| DEBUG   | 상세 디버깅 정보 (개발 환경)        | "Event 생성: eventId=123"     |

### 11.2 GlobalExceptionHandler 로깅 추가

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        if (e.getErrorCode().getStatus().is5xxServerError()) {
            log.error("[CustomException] {}: {}", e.getErrorCode(), e.getMessage(), e);
        } else {
            log.warn("[CustomException] {}: {}", e.getErrorCode(), e.getMessage());
        }

        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity
            .status(e.getErrorCode().getStatus())
            .body(response);
    }
}
```

---

## 12. 요약

Day Memory 프로젝트의 예외 처리는 다음 원칙을 따릅니다:

1. **일관된 에러 응답 형식** (status, code, message, timestamp)
2. **도메인별 에러 코드 접두사** (USER_, EVENT_, GIFT_, REMINDER_)
3. **HTTP 상태 코드 명확한 매핑** (400/401/403/404/409/500/503)
4. **CustomException + ErrorCode enum** 구조
5. **GlobalExceptionHandler**로 전역 예외 처리
6. **@Valid 검증 실패 시 상세한 필드 에러 응답**
7. **로그 레벨 정책** (ERROR: 5xx, WARN: 4xx)

이를 통해 프론트엔드와의 명확한 에러 통신과 디버깅 효율성을 확보합니다.

---

**작성일**: 2025-01-10
**버전**: 1.0
