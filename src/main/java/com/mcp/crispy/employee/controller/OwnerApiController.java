package com.mcp.crispy.employee.controller;


import com.mcp.crispy.common.exception.EmployeeNotFoundException;
import com.mcp.crispy.common.validator.ValidationService;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import com.mcp.crispy.employee.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owner")
public class OwnerApiController {

    private final ValidationService validationService;
    private final OwnerService ownerService;

    // 직원 등록
    @PostMapping("/employee/register/v1")
    public ResponseEntity<?> registerEmployee(@Valid @RequestBody EmployeeRegisterDto employeeRegisterDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        validationService.validateEmployee(employeeRegisterDto);
        ownerService.registerEmployee(employeeRegisterDto, authentication);

        return ResponseEntity.ok(Map.of("message", "직원이 등록되었습니다."));
    }

    @GetMapping("/employees/{frnNo}/v1")
    public ResponseEntity<?> getAllEmployees(@PathVariable int frnNo,
                                             @RequestParam(required = false) Integer empStat,
                                             @RequestParam(required = false) Integer position) {
        try {
            log.info("getAllEmployees: {} {} {}", frnNo, position, empStat);
            List<EmployeeDto> employees = ownerService.getEmployeesByFrnNo(frnNo, empStat, position);
            return ResponseEntity.ok(employees);
        } catch (EmployeeNotFoundException e) {
            // 여기서 클라이언트에 적절한 에러 메시지와 함께 응2답을 반환합니다.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 직원 삭제
    @DeleteMapping("/employee/{empNo}/v1")
    public ResponseEntity<?> removeEmployee(@PathVariable int empNo) {
        ownerService.removeEmployeeById(empNo);
        return ResponseEntity.ok().build();
    }

    // 선택한 직원 삭제
    @DeleteMapping("/employees/v1")
    public ResponseEntity<?> removeEmployees(@RequestBody List<Integer> empNos) {
        ownerService.removeEmployees(empNos);
        return ResponseEntity.ok().build();
    }

}
