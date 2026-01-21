package com.hungshop.hunghypebeast.service.impl;

import com.hungshop.hunghypebeast.config.SePayProperties;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.service.SePayClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class SePayClientImpl implements SePayClient {

    private final SePayProperties properties;

    @Override
    public SePayPaymentResponse createQrPayment(Order order, long amount) {
        if (!properties.isEnabled()) {
            log.info("[SePay] Integration disabled, skipping real API call. Returning dummy QR info.");
            String dummyRef = "DEMO-" + order.getOrderNumber();
            String dummyQr = "https://sepay.vn/qr/" + dummyRef;
            return new SePayPaymentResponse(dummyQr, dummyRef);
        }

        try {
            // Map các field theo spec "Tạo form thanh toán" của SePay
            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("merchant", properties.getMerchantId());
            fields.put("currency", "VND");
            fields.put("order_amount", String.valueOf(amount));
            fields.put("operation", "PURCHASE");
            fields.put("order_description", "Thanh toán đơn hàng " + order.getOrderNumber());
            // Dùng orderNumber làm order_invoice_number (phải đảm bảo unique)
            fields.put("order_invoice_number", order.getOrderNumber());
            // Mặc định thanh toán bằng chuyển khoản ngân hàng (hiển thị QR)
            fields.put("payment_method", "BANK_TRANSFER");

            if (properties.getSuccessUrl() != null) {
                fields.put("success_url", properties.getSuccessUrl());
            }
            if (properties.getErrorUrl() != null) {
                fields.put("error_url", properties.getErrorUrl());
            }
            if (properties.getCancelUrl() != null) {
                fields.put("cancel_url", properties.getCancelUrl());
            }

            // Tạo signature HMAC-SHA256 theo đúng thứ tự field
            String signature = signFields(fields, properties.getSecretKey());
            fields.put("signature", signature);

            String checkoutUrl = buildCheckoutUrl(properties.getBaseUrl(), fields);

            log.info("[SePay] Generated checkout URL for order {}", order.getOrderNumber());
            // Sử dụng checkoutUrl như nơi user sẽ được redirect tới để thanh toán
            // Và dùng orderNumber làm reference để map IPN về sau
            return new SePayPaymentResponse(checkoutUrl, order.getOrderNumber());
        } catch (Exception ex) {
            log.error("[SePay] Failed to generate SePay checkout URL for order {}: {}", order.getOrderNumber(), ex.getMessage());
            throw new IllegalStateException("Failed to generate SePay checkout URL: " + ex.getMessage(), ex);
        }
    }

    private String signFields(Map<String, String> fields, String secretKey) throws Exception {
        // Danh sách field được phép ký theo tài liệu SePay
        List<String> allowed = new ArrayList<>();
        allowed.add("merchant");
        allowed.add("operation");
        allowed.add("payment_method");
        allowed.add("order_amount");
        allowed.add("currency");
        allowed.add("order_invoice_number");
        allowed.add("order_description");
        allowed.add("customer_id");
        allowed.add("success_url");
        allowed.add("error_url");
        allowed.add("cancel_url");

        List<String> parts = new ArrayList<>();
        // Giữ nguyên thứ tự key như khi build fields (giống ví dụ PHP trong docs)
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String name = entry.getKey();
            if (!allowed.contains(name)) {
                continue;
            }
            String value = entry.getValue() != null ? entry.getValue() : "";
            parts.add(name + "=" + value);
        }

        String toSign = String.join(",", parts);

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(toSign.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(rawHmac);
    }

    private String buildCheckoutUrl(String baseUrl, Map<String, String> fields) {
        if (baseUrl == null || baseUrl.isBlank()) {
            // Theo docs, base sandbox là https://pgapi-sandbox.sepay.vn
            baseUrl = "https://pgapi-sandbox.sepay.vn";
        }
        StringJoiner joiner = new StringJoiner("&");
        fields.forEach((key, value) -> {
            if (value == null) {
                return;
            }
            String encodedKey = urlEncode(key);
            String encodedValue = urlEncode(value);
            joiner.add(encodedKey + "=" + encodedValue);
        });

        String query = joiner.toString();
        return baseUrl.endsWith("?") ? baseUrl + query : baseUrl + "?" + query;
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
