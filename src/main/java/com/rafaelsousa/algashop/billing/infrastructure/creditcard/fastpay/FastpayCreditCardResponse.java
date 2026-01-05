package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastpayCreditCardResponse {
    private String id;
    private String lastNumbers;
    private Integer expMonth;
    private Integer expYear;
    private String brand;
}