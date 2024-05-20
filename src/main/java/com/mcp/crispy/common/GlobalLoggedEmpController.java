package com.mcp.crispy.common;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.admin.service.AdminService;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalLoggedEmpController {

    private final EmployeeService employeeService;
    private final AdminService adminService;

    @ModelAttribute
    public void addAttribute(Principal principal, Model model) {
        if(principal != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String getEmpId = authentication.getName();
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (isAdmin) {
                    // 관리자 일 때
                    AdminDto admin = adminService.getAdmin(getEmpId);
                    model.addAttribute("isAdmin", admin);
                } else {
                    EmployeeDto loggedEmp = employeeService.getEmployeeName(getEmpId);
                    model.addAttribute("loggedEmp", loggedEmp);
                }
            }
        }
    }

}
