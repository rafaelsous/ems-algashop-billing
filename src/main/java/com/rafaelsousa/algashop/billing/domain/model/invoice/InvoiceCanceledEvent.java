package com.rafaelsousa.algashop.billing.domain.model.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class InvoiceCanceledEvent {
    private UUID invoiceId;
    private UUID customerId;
    private String orderId;
    private OffsetDateTime canceledAt;
}