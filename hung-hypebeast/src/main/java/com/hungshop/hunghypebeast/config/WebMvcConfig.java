package com.hungshop.hunghypebeast.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CartContextInterceptor cartContextInterceptor;
        private final AdminAuthInterceptor adminAuthInterceptor;

    // Chuỗi các path, ngăn cách bởi dấu phẩy, cấu hình trong application.yml
    @Value("${app.cart-context.paths}")
    private String cartContextPaths;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] patterns = cartContextPaths.split(",");

        registry.addInterceptor(cartContextInterceptor)
                    .addPathPatterns(patterns);

            // Bảo vệ các endpoint admin bằng header X-Admin-Token
            registry.addInterceptor(adminAuthInterceptor)
                    .addPathPatterns("/api/admin/**");
    }
}
