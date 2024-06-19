package com.mcp.crispy.common.validator;

import com.mcp.crispy.common.exception.PasswordException;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.dto.NewPasswordDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewPasswordValidator extends AbstractValidator<NewPasswordDto>{

    public void validatePassword(NewPasswordDto newPasswordDto, EmployeeDto employeeDto) {

        if (!newPasswordDto.getNewPassword().equals(newPasswordDto.getConfirmPassword())) {
            log.info("비밀번호 확인 : {} {}", newPasswordDto.getNewPassword(), newPasswordDto.getConfirmPassword());
            throw new PasswordException("NotMatchPassword", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }

    @Override
    protected void doValidate(NewPasswordDto dto, Errors errors) {

    }
}
