package com.hungshop.hunghypebeast.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {
    @NotBlank
    private String customerName;

    @NotBlank
    private String customerPhone;

    @Email
    private String customerEmail;

    @NotBlank
    private String customerAddress;

    @NotNull
    private String paymentMethod; // COD, SEPAY, ...

    private String note;
}
