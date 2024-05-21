package com.mcp.crispy.employee.controller;

import com.mcp.crispy.common.userdetails.CustomDetails;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/crispy/owner")
public class OwnerController {

    private final EmployeeService employeeService;

    @GetMapping("/employee/register")
    public String registerEmployee() {
        return "owner/employee-register";
    }

//    @IsOwner
    @GetMapping("/employees")
    public String getListEmployees(Authentication authentication, Model model) {
        CustomDetails userDetails = (CustomDetails) authentication.getPrincipal();
        int frnNo = userDetails.getFrnNo();
        model.addAttribute("frnNo", frnNo);
        return "owner/employees";
    }

    // 관리자가 직원 정보 정보 수정할때 사용하는 페이지 가입승인 필요시 가입승인 버튼 동적으로 생성해줌
    @GetMapping("/employee")
    public String employee(Principal principal, Model model) {
        EmployeeDto employee = employeeService.getEmployeeName(principal.getName());
        model.addAttribute("employee", employee);
        return "owner/employee-detail";
    }
}
