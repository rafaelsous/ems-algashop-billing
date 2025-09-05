package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.AbstractAuditableAggregateRoot;
import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.billing.domain.model.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Invoice extends AbstractAuditableAggregateRoot<Invoice> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private String orderId;
    private UUID customerId;
    private OffsetDateTime issuedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private String cancelReason;
    private OffsetDateTime expiresAt;
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PaymentSettings paymentSettings;

    @Embedded
    private Payer payer;

    @ElementCollection
    @CollectionTable(name = "invoice_line_item", joinColumns = @JoinColumn(name = "invoice_id"))
    private Set<LineItem> items = new HashSet<>();

    public static Invoice issue(String orderId, UUID customerId, Payer payer, Set<LineItem> items) {
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(payer);
        Objects.requireNonNull(items);

        if (!StringUtils.hasText(orderId)) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_INVOICE_ORDER_ID_CANNOT_BE_EMPTY);
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_INVOICE_MUST_HAVE_AT_LEAST_ONE_ITEM);
        }

        BigDecimal totalAmount = items.stream().map(LineItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Invoice invoice = new Invoice(
                IdGenerator.generateTimeBasedUUID(),
                orderId,
                customerId,
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

        invoice.registerEvent(InvoiceIssuedEvent.builder()
                .invoiceId(invoice.getId())
                .customerId(invoice.getCustomerId())
                .orderId(invoice.getOrderId())
                .issuedAt(invoice.getIssuedAt())
                .build());

        return invoice;
    }

    public Set<LineItem> getItems() {
        return Collections.unmodifiableSet(this.items);
    }

    public void markAsPaid() {
        if (!isUnpaid()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_CANNOT_BE_MARKED_AS_PAID
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        setStatus(InvoiceStatus.PAID);
        setPaidAt(OffsetDateTime.now());

        this.registerEvent(InvoicePaidEvent.builder()
                .invoiceId(this.getId())
                .customerId(this.getCustomerId())
                .orderId(this.getOrderId())
                .paidAt(this.getPaidAt())
                .build());
    }

    public void cancel(String cancelReason) {
        if (isCanceled()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_IS_ALREADY_CANCELED.formatted(this.getId()));
        }

        setCancelReason(cancelReason);
        setStatus(InvoiceStatus.CANCELED);
        setCanceledAt(OffsetDateTime.now());

        this.registerEvent(InvoiceCanceledEvent.builder()
                .invoiceId(this.getId())
                .customerId(this.getCustomerId())
                .orderId(this.getOrderId())
                .canceledAt(this.getCanceledAt())
                .build());
    }

    public void assignPaymentGatewayCode(String code) {
        if (!isUnpaid()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_CANNOT_BE_ASSIGNED_PAYMENT_GATEWAY_CODE
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        if (Objects.isNull(this.paymentSettings)) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_HAS_NO_PAYMENT_SETTINGS.formatted(this.getId()));
        }

        this.paymentSettings.assignGatewayCode(code);
    }

    public void changePaymentSettings(PaymentMethod paymentMethod, UUID creditCardId) {
        if (!isUnpaid()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_CANNOT_BE_ASSIGNED_PAYMENT_GATEWAY_CODE
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        this.setPaymentSettings(PaymentSettings.brandNew(paymentMethod, creditCardId));
        paymentSettings.setInvoice(this);
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