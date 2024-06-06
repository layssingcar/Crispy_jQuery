package com.mcp.crispy.common;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized error. Message - {}", authException.getMessage());

        Cookie[] cookies = request.getCookies();
        if (cookies != null && Arrays.stream(cookies).anyMatch(cookie -> "accessToken".equals(cookie.getName()))) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.sendRedirect("/crispy/login"); // 로그인 페이지로 리다이렉트
        }
    }
}
