package com.mcp.crispy.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 사원이 존재하지 않을때 발생
     *
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return ResponseEntity.badRequest().body(Map.of(ex.getField(), ex.getMessage()));
    }

    /**
     * Spring Validation 익셉션
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return getMapResponseEntity(ex.getBindingResult());
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<Map<String, String>> handleCustomValidationException(CustomValidationException ex) {
        return getMapResponseEntity(ex.getBindingResult());
    }

    private static ResponseEntity<Map<String, String>> getMapResponseEntity(BindingResult ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handlerIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Map<String, String >> handlePasswordException(PasswordException ex) {
        return ResponseEntity.badRequest().body(Map.of(ex.getField(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidLoginRequestException.class)
    public ResponseEntity<Map<String, String >> handleInvalidLoginRequestException(InvalidLoginRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getErrors());
    }

}
