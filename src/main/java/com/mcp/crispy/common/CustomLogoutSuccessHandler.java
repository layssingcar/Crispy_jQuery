package com.mcp.crispy.common;

import com.mcp.crispy.auth.service.AuthenticationService;
import com.mcp.crispy.common.utils.CookieUtil;
import com.mcp.crispy.employee.dto.EmployeeDto;
import com.mcp.crispy.employee.service.EmployeeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final EmployeeService employeeService;
    private final AuthenticationService authenticationService;
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if (authentication != null) {
            EmployeeDto employee = employeeService.getEmployeeName(authentication.getName());
            authenticationService.logout(employee.getEmpNo());

            CookieUtil.deleteCookie(response, "accessToken");
            CookieUtil.deleteCookie(response, "refreshToken");

            response.sendRedirect("/crispy/login");
        }
    }
}
