package com.rafaelsousa.algashop.billing.application.invoice.query;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItemOutput {
    private Integer number;
    private String name;
    private BigDecimal amount;
}
