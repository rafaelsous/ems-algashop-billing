package com.rafaelsousa.algashop.billing.infrastructure.payment.fastpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastpayPaymentResponse {
    private String id;
    private BigDecimal totalAmount;
    private FastpayPaymentStatus status;
    private String method;
    private String referenceCode;
}