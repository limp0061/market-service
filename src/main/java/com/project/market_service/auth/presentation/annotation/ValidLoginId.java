package com.project.market_service.auth.presentation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(
        validatedBy = {}
)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "로그인 아이디를 입력해주세요")
@Size(min = 4, max = 20, message = "로그인 아이디는 4자 이상 20자 이하로 입력해주세요")
@ReportAsSingleViolation
@Pattern(
        regexp = "^[a-z0-9]+$",
        message = "로그인 아이디는 영문 소문와 숫자만 사용 가능합니다"
)
public @interface ValidLoginId {
    String message() default "유효하지 않은 아이디 형식입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
