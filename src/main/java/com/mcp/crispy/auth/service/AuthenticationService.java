package com.mcp.crispy.auth.service;

import com.mcp.crispy.admin.dto.AdminDto;
import com.mcp.crispy.auth.domain.AdminPrincipal;
import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.common.config.CrispyUserDetailsService;
import com.mcp.crispy.common.utils.JwtUtil;
import com.mcp.crispy.employee.dto.EmployeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final CrispyUserDetailsService crispyUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 로그인시 토큰 부여
    public UserDetails login(String username, String password) {
        UserDetails userDetails = crispyUserDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new IllegalArgumentException("인증되지 않은 정보입니다.");
        }

        if (userDetails instanceof EmployeePrincipal employeePrincipal) {
            EmployeeDto employee = employeePrincipal.getEmployee();
            String accessToken = jwtUtil.createAccessToken(employeePrincipal);
            String refreshToken = jwtUtil.createRefreshToken(employeePrincipal);
            employee.setAccessToken(accessToken);
            employee.setRefreshToken(refreshToken);
            return employeePrincipal;
        } else if (userDetails instanceof AdminPrincipal adminPrincipal) {
            AdminDto admin = adminPrincipal.getAdmin();
            String accessToken = jwtUtil.createAccessToken(adminPrincipal);
            String refreshToken = jwtUtil.createRefreshToken(adminPrincipal);
            admin.setAccessToken(accessToken);
            admin.setRefreshToken(refreshToken);
            return adminPrincipal;
        }

        return userDetails;
    }


}
