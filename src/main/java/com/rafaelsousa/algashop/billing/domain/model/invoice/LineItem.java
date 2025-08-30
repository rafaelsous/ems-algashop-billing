package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.FieldValidations;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@EqualsAndHashCode
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineItem {
    private Integer number;
    private String name;
    private BigDecimal amount;

    @Builder
    public LineItem(Integer number, String name, BigDecimal amount) {
        Objects.requireNonNull(number);
        FieldValidations.requiresNonBlank(name);
        Objects.requireNonNull(amount);

        if (number <= 0) {
            throw new IllegalArgumentException("Number must be greater than zero");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        this.number = number;
        this.name = name;
        this.amount = amount;
    }
}