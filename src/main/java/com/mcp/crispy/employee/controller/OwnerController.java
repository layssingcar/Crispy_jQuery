package com.mcp.crispy.employee.controller;

import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.common.annotation.IsOwner;
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

    /**
     * 직원 등록
     * 배영욱 (24.05.15)
     * @return forward (employee-register.html)
     */
    @IsOwner
    @GetMapping("/employee/register")
    public String registerEmployee() {
        return "owner/employee-register";
    }

    /**
     * 직원 목록
     * 배영욱 (24. 05. 15)
     * @param authentication 인증 정보
     * @param model 모델 객체
     * @return forward (employees.html)
     */
    @IsOwner
    @GetMapping("/employees")
    public String getListEmployees(Authentication authentication, Model model) {
        EmployeePrincipal userDetails = (EmployeePrincipal) authentication.getPrincipal();
        int frnNo = userDetails.getFrnNo();
        model.addAttribute("frnNo", frnNo);
        return "owner/employees";
    }

    /**
     * 직원 상세 정보
     * 관리자가 직원 정보를 수정할 수 있는 페이지. 가입 승인 버튼을 동적으로 생성.
     * 배영욱(24. 05. 20)
     *
     * @param principal 현재 인증된 사용자 정보
     * @param model 모델 객체
     * @return forward employee-detail.html
     */
    @IsOwner
    @GetMapping("/employeeDetail")
    public String employee(Principal principal, Model model) {
        EmployeeDto employee = employeeService.getEmployeeName(principal.getName());
        model.addAttribute("employee", employee);
        return "owner/employee-detail";
    }
}
