package com.hungshop.hunghypebeast.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sepay")
public class SePayProperties {
    private boolean enabled;
    /**
     * Base URL dùng để tạo link checkout, ví dụ:
     * Sandbox: https://pgapi-sandbox.sepay.vn
     * Production: https://pgapi.sepay.vn
     */
    private String baseUrl;

    /** Merchant ID trên màn hình tích hợp SePay */
    private String merchantId;

    /** Secret key dùng để ký HMAC-SHA256 */
    private String secretKey;

    /** URL redirect khi thanh toán thành công */
    private String successUrl;

    /** URL redirect khi thanh toán lỗi */
    private String errorUrl;

    /** URL redirect khi khách hủy thanh toán */
    private String cancelUrl;

    /** IPN callback URL (nếu cấu hình) */
    private String callbackUrl;
}
