package com.mcp.crispy.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
