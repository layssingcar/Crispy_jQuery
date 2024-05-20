package com.mcp.crispy.email.mapper;

import com.mcp.crispy.email.dto.EmailVerificationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EmailVerificationMapper {
    @Select("SELECT VERIFY_EMAIL, VERIFY_END_DT, VERIFY_CODE FROM VERIFICATION_T WHERE VERIFY_EMAIL = #{verifyEmail} ORDER BY VERIFY_END_DT DESC FETCH FIRST 1 ROWS ONLY")
    List<EmailVerificationDto> findByEmail(@Param("verifyEmail") String verifyEmail);

    void insertVerification(EmailVerificationDto emailVerificationDto);

    @Update("UPDATE VERIFICATION_T SET VERIFY_STAT = 2 WHERE VERIFY_EMAIL = #{verifyEmail} AND VERIFY_STAT = 0")
    void expiredPreviousCodes(@Param("verifyEmail") String verifyEmail);

    @Update("UPDATE VERIFICATION_T SET VERIFY_STAT = #{verifyStat} WHERE VERIFY_EMAIL = #{verifyEmail} AND VERIFY_CODE = #{verifyCode}")
    void updateCodeStatus(@Param("verifyEmail") String verifyEmail, @Param("verifyCode") String verifyCode, @Param("verifyStat") int verifyStat);

    @Select("SELECT VERIFY_END_DT FROM VERIFICATION_T WHERE VERIFY_EMAIL = #{verifyEmail} ORDER BY VERIFY_END_DT DESC FETCH FIRST 1 ROWS ONLY")
    LocalDateTime getExpiryDateTime(@Param("verifyEmail") String verifyEmail);
}
