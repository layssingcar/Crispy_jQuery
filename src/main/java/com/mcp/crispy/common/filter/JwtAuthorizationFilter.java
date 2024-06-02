package com.mcp.crispy.common.filter;

import com.mcp.crispy.auth.domain.AdminPrincipal;
import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.common.config.CrispyUserDetailsService;
import com.mcp.crispy.common.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CrispyUserDetailsService crispyUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (isExcludedPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }



        String header = request.getHeader(JwtUtil.HEADER);
        if (header == null) {
            // 쿠키에서 토큰 가져오기
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        header = JwtUtil.TOKEN_PREFIX + cookie.getValue();
                        break;
                    }
                }
            }
        }
        log.info("Authorization Header: {}", header);

        if (header != null && header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            String token = header.replace(JwtUtil.TOKEN_PREFIX, "");
            log.info("JWT Token: {}", token);

            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    log.info("Username from token: {}", username);

                    UserDetails userDetails = crispyUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication;
                    if (userDetails instanceof EmployeePrincipal employeePrincipal) {
                        authentication = new UsernamePasswordAuthenticationToken(
                                employeePrincipal, null, employeePrincipal.getAuthorities());
                    } else if (userDetails instanceof AdminPrincipal adminPrincipal) {
                        authentication = new UsernamePasswordAuthenticationToken(
                                adminPrincipal, null, adminPrincipal.getAuthorities());
                    } else {
                        throw new IllegalStateException("Unknown user type: " + userDetails.getClass().getName());
                    }

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.error("JWT Authentication failed", e);
            }
        }

        // 로깅 시작 시간
        long start = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            // 로깅 종료 시간 및 실행 시간 계산
            long executionTime = System.currentTimeMillis() - start;
            log.info("요청 [{}] 완료: 실행 시간 {} ms", requestURI, executionTime);
        }
    }

    private boolean isExcludedPath(String requestURI) {
        return requestURI.equals("/") ||
                requestURI.equals("/crispy") ||
                requestURI.equals("/crispy/") ||
                requestURI.equals("/CRISPY") ||
                requestURI.equals("/CRISPY/") ||
                requestURI.equals("/crispy/api/auth/login/v1") ||
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/img/") ||
                requestURI.startsWith("/resources/") ||
                requestURI.startsWith("/profiles/") ||
                requestURI.startsWith("/upload/") ||
                requestURI.startsWith("/franchise/") ||
                requestURI.startsWith("/crispy_img/") ||
                requestURI.equals("/crispy/login");
    }
}
