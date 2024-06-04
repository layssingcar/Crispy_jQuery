package com.mcp.crispy.common.validator;

import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
@RequiredArgsConstructor
public class CheckEmpIdValidator extends AbstractValidator<EmployeeRegisterDto> {

    private final EmployeeService employeeService;

    @Override
    protected void doValidate(EmployeeRegisterDto employeeRegisterDto, Errors errors) {
        if (employeeService.existsByEmpId(employeeRegisterDto.getEmpId())) {
            errors.rejectValue("empId", "empId", "이미 사용중인 아이디입니다.");
        }
    }
}
