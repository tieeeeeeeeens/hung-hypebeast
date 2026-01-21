package com.hungshop.hunghypebeast.service;

import com.hungshop.hunghypebeast.entity.Order;

public interface SePayClient {

    class SePayPaymentResponse {
        private final String qrCodeUrl;
        private final String reference;

        public SePayPaymentResponse(String qrCodeUrl, String reference) {
            this.qrCodeUrl = qrCodeUrl;
            this.reference = reference;
        }

        public String getQrCodeUrl() {
            return qrCodeUrl;
        }

        public String getReference() {
            return reference;
        }
    }

    SePayPaymentResponse createQrPayment(Order order, long amount);
}
