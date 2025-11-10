package com.daymemory.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     */
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

    /**
     * @Valid 검증 실패 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        log.warn("[ValidationException] Validation failed: {}", e.getMessage());

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

    /**
     * 예상하지 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[Exception] Unexpected error occurred: {}", e.getMessage(), e);

        ErrorResponse response = ErrorResponse.of(ErrorCode.SERVER_INTERNAL_ERROR);
        return ResponseEntity
                .status(ErrorCode.SERVER_INTERNAL_ERROR.getStatus())
                .body(response);
    }

    /**
     * 에러 응답 DTO
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ErrorResponse {
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

    /**
     * Validation 에러 응답 DTO
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class ValidationErrorResponse {
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

    /**
     * 필드 에러 상세 정보
     */
    @Getter
    @AllArgsConstructor
    public static class FieldErrorDetail {
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
}
