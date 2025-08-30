package com.rafaelsousa.algashop.billing.domain.model.invoice;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invoice {

    @EqualsAndHashCode.Include
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

    public Set<LineItem> getItems() {
        return Collections.unmodifiableSet(this.items);
    }

    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = OffsetDateTime.now();
    }

    public void cancel(String reason) {
        this.status = InvoiceStatus.CANCELED;
        this.cancelReason = reason;
        this.canceledAt = OffsetDateTime.now();
    }

    public void assignPaymentGatewayCode(String code) {

    }

    public void changePaymentSettings(PaymentMethod paymentMethod, UUID creditCardId) {

    }

    public boolean isPaid() {
        return this.status == InvoiceStatus.PAID;
    }

    public boolean isUnpaid() {
        return !this.isPaid();
    }

    public boolean isCanceled() {
        return this.status == InvoiceStatus.CANCELED;
    }
}