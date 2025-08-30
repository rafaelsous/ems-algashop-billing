package com.rafaelsousa.algashop.billing.domain.model.invoice;

import java.util.Objects;
import java.util.UUID;

public class PaymentSettings {
    private UUID id;
    private UUID creditcardId;
    private String gatewayCode;
    private PaymentMethod method;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentSettings that = (PaymentSettings) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}