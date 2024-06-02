package com.mcp.crispy.email.service;

import com.mcp.crispy.email.dto.EmailVerificationDto;
import com.mcp.crispy.email.mapper.EmailVerificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.mcp.crispy.common.utils.RandomCodeUtils.generateVerificationCode;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationMapper emailVerificationMapper;
    private final EmailService emailService;

    //인증번호 전송 및 인증테이블에 저장
    @Transactional
    public void sendAndSaveVerificationCode(String verifyEmail) {

        //기존 인증코드 만료
        emailVerificationMapper.expiredPreviousCodes(verifyEmail);

        String verificationCode = generateVerificationCode();
        EmailVerificationDto emailVerificationDto = EmailVerificationDto.builder()
                .verifyEmail(verifyEmail)
                .verifyCode(verificationCode)
                .verifyStat(0) // 0 : NEW
                .build();

        emailVerificationMapper.insertVerification(emailVerificationDto);
        emailService.sendVerificationEmail(verifyEmail, verificationCode);
    }


    @Transactional
    public boolean verifyCode(String email, String code) {
        List<EmailVerificationDto> emailVerificationDtos = emailVerificationMapper.findByEmail(email);
        for (EmailVerificationDto emailVerification : emailVerificationDtos) {
            if(emailVerification.getVerifyStat() == 0) { // 0인지 확인
                if (isCodeExpired(emailVerification.getVerifyEmail())) {
                    // 만료된 코드 2 : EXPIRED 로 업데이트
                    emailVerificationMapper.updateCodeStatus(emailVerification.getVerifyEmail(), emailVerification.getVerifyCode(), 2);
                } else if(emailVerification.getVerifyCode().equals(code)) {
                    // 유효한 코드 사용시 상태를 1 : USED 로 업데이트
                    emailVerificationMapper.updateCodeStatus(emailVerification.getVerifyEmail(), emailVerification.getVerifyCode(), 1);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isCodeExpired(String verifyEmail) {
        LocalDateTime expiryDateTime = emailVerificationMapper.getExpiryDateTime(verifyEmail);
        return LocalDateTime.now().isAfter(expiryDateTime);
    }

}
