package com.hungshop.hunghypebeast.controller;

import com.hungshop.hunghypebeast.config.SePayProperties;
import com.hungshop.hunghypebeast.entity.Order;
import com.hungshop.hunghypebeast.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SePayTestController {

    private final OrderRepository orderRepository;
    private final SePayProperties sePayProperties;

    /**
     * Simple helper endpoint to test real SePay sandbox flow.
     * Usage: open in browser
     *   GET /sepay/test-form?orderNumber=HH-XXXXXXX
     * It will auto-submit a POST form to https://pay-sandbox.sepay.vn/v1/checkout/init
     * with the same fields/signature as SePay docs, so bạn thấy được UI thanh toán.
     */
    @GetMapping(value = "/sepay/test-form", produces = MediaType.TEXT_HTML_VALUE)
    public String redirectToSePay(@RequestParam("orderNumber") String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));

        long amount = order.getTotalAmount();

        try {
            Map<String, String> fields = buildFormFields(order, amount);

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Redirecting to SePay</title></head>");
            html.append("<body onload='document.forms[0].submit()'>");
            html.append("<h3>Đang chuyển tới trang thanh toán SePay sandbox...</h3>");
            html.append("<form method='POST' action='https://pay-sandbox.sepay.vn/v1/checkout/init'>");

            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String name = HtmlUtils.htmlEscape(entry.getKey());
                String value = HtmlUtils.htmlEscape(entry.getValue());
                html.append("<input type='hidden' name='")
                        .append(name)
                        .append("' value='")
                        .append(value)
                        .append("'/>");
            }

            html.append("<noscript><button type='submit'>Chuyển tới SePay</button></noscript>");
            html.append("</form></body></html>");

            return html.toString();
        } catch (Exception e) {
            log.error("[SePayTest] Failed to build form for order {}: {}", orderNumber, e.getMessage());
            return "<html><body><h3>Lỗi: " + HtmlUtils.htmlEscape(e.getMessage()) + "</h3></body></html>";
        }
    }

    private Map<String, String> buildFormFields(Order order, long amount) throws Exception {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("merchant", sePayProperties.getMerchantId());
        fields.put("currency", "VND");
        fields.put("order_amount", String.valueOf(amount));
        fields.put("operation", "PURCHASE");
        fields.put("order_description", "Thanh toán đơn hàng " + order.getOrderNumber());
        fields.put("order_invoice_number", order.getOrderNumber());
        fields.put("payment_method", "BANK_TRANSFER");

        if (sePayProperties.getSuccessUrl() != null) {
            String base = sePayProperties.getSuccessUrl();
            String successUrl = base.contains("?")
                    ? base + "&order=" + order.getOrderNumber()
                    : base + "?order=" + order.getOrderNumber();
            fields.put("success_url", successUrl);
        }
        if (sePayProperties.getErrorUrl() != null) {
            fields.put("error_url", sePayProperties.getErrorUrl());
        }
        if (sePayProperties.getCancelUrl() != null) {
            fields.put("cancel_url", sePayProperties.getCancelUrl());
        }

        String signature = signFields(fields, sePayProperties.getSecretKey());
        fields.put("signature", signature);
        return fields;
    }

    private String signFields(Map<String, String> fields, String secretKey) throws Exception {
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
        // Duyệt theo thứ tự key đã build trong fields, giống code mẫu PHP
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

        return java.util.Base64.getEncoder().encodeToString(rawHmac);
    }
}
