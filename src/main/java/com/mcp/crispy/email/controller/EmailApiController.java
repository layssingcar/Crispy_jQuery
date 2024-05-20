package com.mcp.crispy.email.controller;

import com.mcp.crispy.email.dto.EmailSendDto;
import com.mcp.crispy.email.dto.EmailVerificationDto;
import com.mcp.crispy.email.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailApiController {

    private final AuthenticationService authenticationService;

    @PostMapping("/api/v1/email/send-verification-code")
    // 인증 코드 전송
    public ResponseEntity<?> sendVerificationCode(@Valid @RequestBody EmailSendDto emailSendDto) {
        String verifyEmail = emailSendDto.getVerifyEmail();
        try {
            authenticationService.sendAndSaveVerificationCode(verifyEmail);
            return ResponseEntity.ok().body(Map.of("message", "인증번호가 전송되었습니다."));
        } catch (Exception ex) {
            log.error("인증번호 전송 중 오류 발생: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "인증번호 전송 중 오류가 발생했습니다."));
        }
    }

    // 인증 코드 검증
    @PostMapping("/api/v1/email/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody EmailVerificationDto emailVerificationDto) {
        String verifyEmail = emailVerificationDto.getVerifyEmail();
        String verifyCode = emailVerificationDto.getVerifyCode();

        log.info("인증 코드 검증 요청: verifyEmail = {}, verifyCode = {}", verifyEmail, verifyCode);

        try {
            boolean isCodeValid = authenticationService.verifyCode(verifyEmail, verifyCode);
            if (isCodeValid) {
                log.info("인증 성공: verifyEmail={}", verifyEmail);
                return ResponseEntity.ok().body(Map.of("message", "인증 성공"));
            } else {
                log.info("인증 실패: verifyEmail={}", verifyEmail);
                return ResponseEntity.badRequest().body(Map.of("error", "인증 실패"));
            }
        } catch (Exception ex) {
            log.error("인증 코드 검증 중 오류 발생: verifyEmail={}, error={}", verifyEmail, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body(Map.of("error", "인증 코드 검증 중 오류가 발생했습니다."));
        }
    }
}
