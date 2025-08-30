package com.rafaelsousa.algashop.billing.domain.model.invoice;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Invoice {
    private UUID id;
    private String customerId;
    private OffsetDateTime issuedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private String cancelReason;
    private OffsetDateTime expiresAt;
    private BigDecimal totalAmount;
    private InvoiceStatus status;
    private PaymentSettings paymentSettings;
    private Payer payer;
    private Set<LineItem> items = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}