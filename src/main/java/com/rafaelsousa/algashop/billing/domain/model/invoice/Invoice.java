package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.DomainException;
import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
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
            throw new IllegalArgumentException(ErrorMessages.ERROR_INVOICE_ORDER_ID_CANNOT_BE_EMPTY);
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_INVOICE_MUST_HAVE_AT_LEAST_ONE_ITEM);
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
        if (!isUnpaid()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_CANNOT_BE_MARKED_AS_PAID
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        setStatus(InvoiceStatus.PAID);
        setPaidAt(OffsetDateTime.now());
    }

    public void cancel(String cancelReason) {
        if (isCanceled()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_IS_ALREADY_CANCELED.formatted(this.getId()));
        }

        setCancelReason(cancelReason);
        setStatus(InvoiceStatus.CANCELED);
        setCanceledAt(OffsetDateTime.now());
    }

    public void assignPaymentGatewayCode(String code) {
        if (!isUnpaid()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_CANNOT_BE_ASSIGNED_PAYMENT_GATEWAY_CODE
                    .formatted(this.getId(), this.getStatus().name().toLowerCase()));
        }

        this.paymentSettings.assignGatewayCode(code);
    }

    public void changePaymentSettings(PaymentMethod paymentMethod, UUID creditCardId) {
        if (!isUnpaid()) {
            throw new DomainException(ErrorMessages.ERROR_INVOICE_CANNOT_BE_ASSIGNED_PAYMENT_GATEWAY_CODE
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