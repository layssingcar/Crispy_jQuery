package com.mcp.crispy.common.annotation;

import com.mcp.crispy.common.validator.NotBlankAndPatternValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {NotBlankAndPatternValidator.class})
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankAndPattern {
    String message() default "유효하지 않은 값입니다";
    String notBlankMessage() default "이 필드는 필수입니다";
    String patternMessage() default "형식이 올바르지 않습니다";
    String pattern() default ".*";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
