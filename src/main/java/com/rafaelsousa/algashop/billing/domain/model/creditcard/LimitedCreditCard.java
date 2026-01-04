package com.rafaelsousa.algashop.billing.domain.model.creditcard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LimitedCreditCard {
    private String lastNumbers;
    private String brand;
    private Integer expMonth;
    private Integer expYear;
    private String gatewayCode;
}