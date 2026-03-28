package com.project.market_service.auth.presentation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
@NotBlank(message = "비밀번호를 입력해주세요")
@ReportAsSingleViolation
@Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d$@$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상, 영문+숫자 조합이어야 합니다"
)
public @interface ValidPassword {
    String message() default "유효하지 않은 비밀번호 형식입니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
