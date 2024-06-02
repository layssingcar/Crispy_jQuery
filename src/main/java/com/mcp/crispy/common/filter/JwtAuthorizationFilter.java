package com.mcp.crispy.common.filter;

import com.mcp.crispy.auth.domain.AdminPrincipal;
import com.mcp.crispy.auth.domain.EmployeePrincipal;
import com.mcp.crispy.common.config.CrispyUserDetailsService;
import com.mcp.crispy.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

        if (isExcludedPath(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        log.info("Authorization Token: {}", token);

        if (token != null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.verify(token);
                    log.info("claims: {}", claims);
                    String username = claims.getSubject();
                    log.info("Username from token: {}", username);

                    UserDetails userDetails = crispyUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = getAuthentication(userDetails, request);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.info("Invalid JWT token");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }
            } catch (ExpiredJwtException ex) {
                log.info("Expired JWT token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Expired JWT token");
                return;
            } catch (Exception e) {
                log.error("JWT Authentication failed", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Authentication failed");
                return;
            }
        } else {
            log.info("Token is null");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is null");
            return;
        }
        logRequestExecutionTime(request, chain, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(JwtUtil.HEADER);
        if (header == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        return cookie.getValue();
                    }
                }
            }
        } else if (header.startsWith(JwtUtil.TOKEN_PREFIX)) {
            return header.replace(JwtUtil.TOKEN_PREFIX, "");
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = null;
        if (userDetails instanceof EmployeePrincipal employeePrincipal) {
            authentication = new UsernamePasswordAuthenticationToken(employeePrincipal, null, employeePrincipal.getAuthorities());
        } else if (userDetails instanceof AdminPrincipal adminPrincipal) {
            authentication = new UsernamePasswordAuthenticationToken(adminPrincipal, null, adminPrincipal.getAuthorities());
        }
        if (authentication != null) {
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        return authentication;
    }

    private void logRequestExecutionTime(HttpServletRequest request, FilterChain chain, HttpServletResponse response) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            log.info("요청 [{}] 완료: 실행 시간 {} ms", request.getRequestURI(), executionTime);
        }
    }

    private boolean isExcludedPath(String requestURI) {
        return requestURI.equals("/") ||
                requestURI.equals("/crispy") ||
                requestURI.equals("/crispy/") ||
                requestURI.equals("/CRISPY") ||
                requestURI.equals("/CRISPY/") ||
                requestURI.equals("/api/auth/login/v1") ||
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
