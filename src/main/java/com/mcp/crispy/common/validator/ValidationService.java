package com.mcp.crispy.common.validator;

import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final CheckEmpIdValidator checkEmpIdValidator;
    private final CheckEmpEmailValidator checkEmpEmailValidator;

    public void validateEmployee(EmployeeRegisterDto employeeRegisterDto, BindingResult bindingResult) {
        checkEmpIdValidator.validate(employeeRegisterDto, bindingResult);
        checkEmpEmailValidator.validate(employeeRegisterDto, bindingResult);
    }
}
