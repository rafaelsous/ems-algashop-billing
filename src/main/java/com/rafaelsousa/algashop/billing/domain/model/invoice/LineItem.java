package com.rafaelsousa.algashop.billing.domain.model.invoice;

import com.rafaelsousa.algashop.billing.domain.model.ErrorMessages;
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
            throw new IllegalArgumentException(ErrorMessages.ERROR_LINE_ITEM_NUMBER_MUST_BE_GREATER_THAN_ZERO);
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(ErrorMessages.ERROR_LINE_ITEM_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        this.number = number;
        this.name = name;
        this.amount = amount;
    }
}