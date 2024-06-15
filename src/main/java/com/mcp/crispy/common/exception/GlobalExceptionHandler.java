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
     * 배영욱 (24. 05. 20)
     * @param ex EmployeeNotFoundException 예외 객체
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return ResponseEntity.badRequest().body(Map.of(ex.getField(), ex.getMessage()));
    }

    /**
     * Spring Validation 예외 처리
     * 배영욱 (24. 05. 20)
     * @param ex MethodArgumentNotValidException 예외 객체
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return getMapResponseEntity(ex.getBindingResult());
    }

    /**
     * 커스텀 Validation 예외를 처리합니다.
     * 배영욱 (24. 06. 12)
     * @param ex CustomValidationException 예외 객체
     */
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

    /**
     * IllegalArgumentException 예외를 처리
     * 배영욱 (24. 05. 20)
     * @param ex IllegalArgumentException 예외 객체
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handlerIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    /**
     * PasswordException 예외를 처리
     * 배영욱 (24. 06. 03)
     * @param ex PasswordException 예외 객체
     */
    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Map<String, String >> handlePasswordException(PasswordException ex) {
        return ResponseEntity.badRequest().body(Map.of(ex.getField(), ex.getMessage()));
    }

    /**
     * 유효하지 않은 로그인 요청 예외 처리
     * 배영욱 (24.06.03)
     * @param ex InvalidLoginRequestException 예외 객체
     */
    @ExceptionHandler(InvalidLoginRequestException.class)
    public ResponseEntity<Map<String, String >> handleInvalidLoginRequestException(InvalidLoginRequestException ex) {
        return ResponseEntity.badRequest().body(ex.getErrors());
    }

}
