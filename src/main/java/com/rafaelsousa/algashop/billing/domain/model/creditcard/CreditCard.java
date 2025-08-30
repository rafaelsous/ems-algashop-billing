package com.rafaelsousa.algashop.billing.domain.model.creditcard;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class CreditCard {
    private UUID id;
    private UUID customerId;
    private OffsetDateTime createdAt;
    private String lastNumbers;
    private String brand;
    private Integer expMonth;
    private Integer expYear;
    private String gatewayCode;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreditCard that = (CreditCard) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}