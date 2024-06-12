package com.mcp.crispy.common.validator;

import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckEmpEmailValidator<T> extends AbstractValidator<T>{

    private final EmployeeService employeeService;

    @Override
    protected void doValidate(T target, Errors errors) {
        if (target instanceof EmployeeRegisterDto employeeRegisterDto) {
            if (employeeService.existsByEmail(employeeRegisterDto.getEmpEmail())) {
                errors.rejectValue("empEmail", "empEmail", "이미 사용중인 이메일입니다.");
            }
        } else {
            if (target instanceof OwnerRegisterDto ownerRegisterDto) {
                if (employeeService.existsByEmail(ownerRegisterDto.getEmpEmail())) {
                    errors.rejectValue("empEmail", "empEmail", "이미 사용중인 이메일입니다.");
                }
            }
        }
    }
}
