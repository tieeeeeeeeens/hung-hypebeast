package com.hungshop.hunghypebeast.config;

import com.hungshop.hunghypebeast.dto.CartContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CartContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String sessionId = request.getHeader("X-Session-Id");
        String userIdHeader = request.getHeader("X-User-Id");

        Long userId = null;
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            try {
                userId = Long.parseLong(userIdHeader);
            } catch (NumberFormatException ignored) {
                // ignore invalid user id, keep it null
            }
        }

        CartContext context = CartContext.builder()
                .sessionId(sessionId)
                .userId(userId)
                .build();

        CartContextHolder.setContext(context);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CartContextHolder.clear();
    }
}
