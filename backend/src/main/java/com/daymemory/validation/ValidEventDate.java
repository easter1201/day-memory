package com.daymemory.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
@Documented
public @interface ValidEventDate {
    String message() default "유효하지 않은 이벤트 날짜입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
