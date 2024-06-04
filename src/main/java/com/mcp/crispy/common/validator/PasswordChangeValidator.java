package com.mcp.crispy.common.validator;

import com.mcp.crispy.common.exception.PasswordException;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.dto.PasswordChangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordChangeValidator extends AbstractValidator<PasswordChangeDto> {

    private final PasswordEncoder passwordEncoder;

    public void validatePassword(PasswordChangeDto passwordChangeDto, EmployeeDto employeeDto) {
        if (!passwordEncoder.matches(passwordChangeDto.getCurrentPassword(), employeeDto.getEmpPw())) {
            log.info("비밀번호 비교: {} {}", passwordChangeDto.getCurrentPassword(), employeeDto.getEmpPw());
            throw new PasswordException("invalidCurrentPassword", "현재 비밀번호가 일치하지 않습니다.");
        }
        if (passwordChangeDto.getNewPassword().equals(passwordChangeDto.getCurrentPassword())) {
            throw new PasswordException("sameAsOld", "새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmPassword())) {
            log.info("비밀번호 확인 : {} {}", passwordChangeDto.getNewPassword(), passwordChangeDto.getConfirmPassword());
            throw new PasswordException("NotMatchPassword", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }

    @Override
    protected void doValidate(PasswordChangeDto dto, Errors errors) {

    }
}
