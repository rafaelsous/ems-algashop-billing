package com.rafaelsousa.algashop.billing.application.invoice.query;

import com.rafaelsousa.algashop.billing.application.invoice.management.PayerData;
import com.rafaelsousa.algashop.billing.domain.model.invoice.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceOutput {
    private UUID id;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime issuedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime expiresAt;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private PayerData payer;
    private PaymentSettingsOutput paymentSettings;
}