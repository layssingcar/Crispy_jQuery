package com.mcp.crispy.common.validator;

import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckEmpIdValidator<T> extends AbstractValidator<T> {

    private final EmployeeService employeeService;

    @Override
    protected void doValidate(T target, Errors errors) {
        if (target instanceof EmployeeRegisterDto employeeRegisterDto) {
            if (employeeService.existsByEmpId(employeeRegisterDto.getEmpId())) {
                errors.rejectValue("empId", "empId", "이미 사용중인 아이디입니다.");
            }
        } else {
            if (target instanceof OwnerRegisterDto ownerRegisterDto) {
                if (employeeService.existsByEmpId(ownerRegisterDto.getEmpId())) {
                    errors.rejectValue("empId", "empId", "이미 사용중인 아이디입니다.");
                }
            }
        }
    }
}
