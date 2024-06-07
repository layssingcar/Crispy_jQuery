package com.mcp.crispy.common.utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Getter
@PropertySource("classpath:jwt.properties")
public class JwtUtil {

    public static final String ISSUER = "moz1mozi.com";
    public static final int EXP_SHORT = 15 * 60 * 1000; // 15분
    public static final int EXP_LONG = 60 * 60 * 1000;  // 1시간
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    private SecretKey key;

    @Value("${jwt.secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String createAccessToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("createAccessToken : {}", username);
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(username) // empId
                .expiration(new Date(System.currentTimeMillis() + EXP_LONG))
                .claim("username", username)
                .claim("roles", authorities)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        log.info("createRefreshToken : {}", username);
        long refreshExp = 7 * 24 * 60 * 60 * 1000; // 7일

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(username) // empId
                .expiration(new Date(System.currentTimeMillis() + refreshExp))
                .signWith(key)
                .compact();
    }

    public Claims verify(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token.replace(TOKEN_PREFIX, ""))
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT expired at: {}. Current time: {}", e.getClaims().getExpiration(), new Date());
            throw e; // re-throw the exception to be handled by the filter
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return null;
        }
    }

    public int getExpiryDurationFromToken(String token) {
        Claims claims = verify(token);
        long expirationMillis = claims.getExpiration().getTime();
        long currentMillis = System.currentTimeMillis();
        return (int) ((expirationMillis - currentMillis) / 1000);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = verify(token);

        return claims.getSubject();
    }

    public UserDetails getUserDetailsFromToken(String token) {
        Claims claims = verify(token);
        String username = claims.getSubject();
        return new User(username, "", List.of());
    }

    public boolean validateToken(String authToken) {
        try {
            verify(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

}