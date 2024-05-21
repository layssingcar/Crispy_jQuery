package com.mcp.crispy.employee.controller;


import com.mcp.crispy.common.exception.EmployeeNotFoundException;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.dto.EmployeeRegisterDto;
import com.mcp.crispy.employee.service.EmployeeService;
import com.mcp.crispy.employee.service.OwnerService;
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
@RequestMapping("/api/v1/owner")
public class OwnerApiController {

    private final OwnerService ownerService;
    private final EmployeeService employeeService;

    @PostMapping("/employee/register")
    public ResponseEntity<?> registerEmployee(@RequestBody EmployeeRegisterDto employeeRegisterDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ownerService.registerEmployee(employeeRegisterDto, authentication);
        return ResponseEntity.ok(Map.of("message", "직원이 등록되었습니다."));
    }

    @GetMapping("/employees/{frnNo}")
    public ResponseEntity<?> getAllEmployees(@PathVariable int frnNo) {
        try {
            List<EmployeeDto> employees = ownerService.getEmployeesByFrnNo(frnNo);
            return ResponseEntity.ok(employees);
        } catch (EmployeeNotFoundException e) {
            // 여기서 클라이언트에 적절한 에러 메시지와 함께 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



}
