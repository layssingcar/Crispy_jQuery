package com.mcp.crispy.common.validator;

import com.mcp.crispy.common.annotation.NotBlankAndPattern;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotBlankAndPatternValidator implements ConstraintValidator<NotBlankAndPattern, String> {

    private String notBlankMessage;
    private String patternMessage;
    private String pattern;
    @Override
    public void initialize(NotBlankAndPattern constraintAnnotation) {
      this.notBlankMessage = constraintAnnotation.notBlankMessage();
      this.patternMessage = constraintAnnotation.patternMessage();
      this.pattern = constraintAnnotation.pattern();;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(notBlankMessage).addConstraintViolation();
            return false;
        }
        if (!value.matches(pattern)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(patternMessage).addConstraintViolation();
            return false;
        }
        return true;
    }
}
