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
    EVENT_NOT_RECURRING(HttpStatus.BAD_REQUEST, "반복 이벤트가 아닙니다."),
    REMINDER_DAYS_INVALID(HttpStatus.BAD_REQUEST, "리마인더 일수는 1 이상이어야 합니다."),

    // Reminder
    REMINDER_NOT_FOUND(HttpStatus.NOT_FOUND, "리마인더를 찾을 수 없습니다."),
    REMINDER_ALREADY_SENT(HttpStatus.CONFLICT, "이미 발송된 리마인더입니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),

    // Gift
    GIFT_NOT_FOUND(HttpStatus.NOT_FOUND, "선물을 찾을 수 없습니다."),
    GIFT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 선물에 접근할 권한이 없습니다."),
    GIFT_CATEGORY_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 선물 카테고리입니다."),

    // File
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 제한을 초과했습니다. (최대 5MB)"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드 가능합니다."),

    // AI
    AI_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "AI 서비스를 사용할 수 없습니다."),
    AI_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI 추천 요청에 실패했습니다."),
    AI_RECOMMENDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "AI 추천 내역을 찾을 수 없습니다."),

    // Shopping API
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "외부 API 호출에 실패했습니다."),

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),
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
