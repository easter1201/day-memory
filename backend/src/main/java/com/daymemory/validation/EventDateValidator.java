package com.daymemory.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class EventDateValidator implements ConstraintValidator<ValidEventDate, LocalDate> {

    @Override
    public void initialize(ValidEventDate constraintAnnotation) {
        // 초기화 로직 (필요시)
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull로 별도 검증
        }

        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusYears(10); // 최대 10년 후까지 허용

        // 과거 날짜이거나 10년 이후 날짜는 거부
        if (value.isBefore(today) || value.isAfter(maxDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "이벤트 날짜는 오늘부터 10년 이내여야 합니다."
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
