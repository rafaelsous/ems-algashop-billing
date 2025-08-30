package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.IdGenerator;
import lombok.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Invoice {

    @EqualsAndHashCode.Include
    private UUID id;
    private String orderId;
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

    public static Invoice issue(String orderId, UUID customerId, Payer payer, Set<LineItem> items) {
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(payer);
        Objects.requireNonNull(items);

        if (!StringUtils.hasText(orderId)) {
            throw new IllegalArgumentException("Order ID cannot be empty");
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least one item");
        }

        BigDecimal totalAmount = items.stream().map(LineItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Invoice(
                IdGenerator.generateTimeBasedUUID(),
                orderId,
                customerId.toString(),
                OffsetDateTime.now(),
                null,
                null,
                null,
                OffsetDateTime.now().plusDays(3),
                totalAmount,
                InvoiceStatus.UNPAID,
                null,
                payer,
                items
        );
    }

    public Set<LineItem> getItems() {
        return Collections.unmodifiableSet(this.items);
    }

    public void markAsPaid() {
        if (isUnpaid()) {
            throw new DomainException("Invoice %s with status %s cannot be marked as paid"
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        setStatus(InvoiceStatus.PAID);
        setPaidAt(OffsetDateTime.now());
    }

    public void cancel(String cancelReason) {
        if (isCanceled()) {
            throw new DomainException("Invoice %s is already canceled".formatted(this.getId()));
        }

        setCancelReason(cancelReason);
        setStatus(InvoiceStatus.CANCELED);
        setCanceledAt(OffsetDateTime.now());
    }

    public void assignPaymentGatewayCode(String code) {
        if (isUnpaid()) {
            throw new DomainException("Invoice %s with status %s cannot be assigned a payment gateway code"
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        this.paymentSettings.assignGatewayCode(code);
    }

    public void changePaymentSettings(PaymentMethod paymentMethod, UUID creditCardId) {
        if (isUnpaid()) {
            throw new DomainException("Invoice %s with status %s cannot be assigned a payment gateway code"
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        this.setPaymentSettings(PaymentSettings.brandNew(paymentMethod, creditCardId));
    }

    public boolean isPaid() {
        return InvoiceStatus.PAID.equals(this.getStatus());
    }

    public boolean isUnpaid() {
        return InvoiceStatus.UNPAID.equals(this.getStatus());
    }

    public boolean isCanceled() {
        return InvoiceStatus.CANCELED.equals(this.getStatus());
    }
}