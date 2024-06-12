package com.mcp.crispy.common.validator;

import com.mcp.crispy.common.exception.CustomValidationException;
import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import com.mcp.crispy.employee.dto.OwnerRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class ValidationService {

    private final CheckEmpIdValidator checkEmpIdValidator;
    private final CheckEmpEmailValidator checkEmpEmailValidator;

    public void validateEmployee(EmployeeRegisterDto employeeRegisterDto) {
        BindingResult bindingResult = new BeanPropertyBindingResult(employeeRegisterDto, "employeeRegisterDto");
        checkEmpIdValidator.validate(employeeRegisterDto, bindingResult);
        checkEmpEmailValidator.validate(employeeRegisterDto, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
    }

    public void validateOwner(OwnerRegisterDto ownerRegisterDto) {
        BindingResult bindingResult = new BeanPropertyBindingResult(ownerRegisterDto, "ownerRegisterDto");
        checkEmpIdValidator.validate(ownerRegisterDto, bindingResult);
        checkEmpEmailValidator.validate(ownerRegisterDto, bindingResult);

        if (bindingResult.hasErrors()) {
            throw new CustomValidationException(bindingResult);
        }
    };
}
