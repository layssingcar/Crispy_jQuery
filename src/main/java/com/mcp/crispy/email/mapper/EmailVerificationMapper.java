package com.mcp.crispy.email.mapper;

import com.mcp.crispy.email.dto.EmailVerificationDto;
import com.mcp.crispy.email.dto.VerifyStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmailVerificationMapper {
    List<EmailVerificationDto> findByEmail(@Param("verifyEmail") String verifyEmail);

    // 인증 코드 삽입
    void insertVerification(EmailVerificationDto emailVerificationDto);

    // 인증 코드 만료로 업데이트
    void expiredPreviousCodes(@Param("verifyEmail") String verifyEmail,
                              @Param("currentStat") VerifyStat currentStat, @Param("verifyStat") VerifyStat verifyStat);

    void updateCodeStatus(@Param("verifyEmail") String verifyEmail, @Param("verifyCode") String verifyCode, @Param("verifyStat") VerifyStat verifyStat);

    LocalDateTime getExpiryDateTime(@Param("verifyEmail") String verifyEmail);
}
