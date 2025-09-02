package com.rafaelsousa.algashop.billing.domain.model.invoice.payment;

import com.rafaelsousa.algashop.billing.domain.model.invoice.Payer;
import com.rafaelsousa.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode
public class PaymentRequest {
    private PaymentMethod method;
    private BigDecimal amount;
    private UUID invoiceId;
    private UUID creditCardId;
    private Payer payer;

    public PaymentRequest(PaymentMethod method, BigDecimal amount, UUID invoiceId, UUID creditCardId, Payer payer) {
        Objects.requireNonNull(method);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(invoiceId);
        Objects.requireNonNull(payer);

        if (PaymentMethod.CREDIT_CARD.equals(method)) {
            Objects.requireNonNull(creditCardId);
        }

        this.method = method;
        this.amount = amount;
        this.invoiceId = invoiceId;
        this.creditCardId = creditCardId;
        this.payer = payer;
    }
}