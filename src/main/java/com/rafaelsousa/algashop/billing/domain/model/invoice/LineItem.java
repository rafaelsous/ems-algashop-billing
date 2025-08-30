package com.rafaelsousa.algashop.billing.domain.model.invoice;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LineItem {
    private Integer number;
    private String name;
    private BigDecimal amount;
}