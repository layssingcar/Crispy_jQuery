package com.mcp.crispy.common;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == 404) {
                return "error/404";
            } else if (statusCode == 403) {
                return "error/403";
            } else if (statusCode == 401) {
                response.sendRedirect("/crispy/login");
                return null;
            } else if (statusCode == 500) {
                return "error/500";
            }
        }
        return "error/error";
    }
}
