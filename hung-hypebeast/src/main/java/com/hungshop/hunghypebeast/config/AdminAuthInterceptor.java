package com.hungshop.hunghypebeast.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Value("${app.admin.api-key:changeme}")
    private String adminApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String headerToken = request.getHeader("X-Admin-Token");

        if (adminApiKey == null || adminApiKey.isBlank()) {
            // Nếu chưa cấu hình api-key thì cho qua (tránh khoá admin nhầm ở môi trường dev)
            return true;
        }

        if (headerToken == null || !adminApiKey.equals(headerToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{" +
                    "\"error\":\"UNAUTHORIZED\"," +
                    "\"message\":\"Missing or invalid X-Admin-Token\"" +
                    "}");
            return false;
        }

        return true;
    }
}
