package com.rafaelsousa.algashop.billing.infrastructure.creditcard.fastpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastpayTokenizedCreditCardModel {
    private String tokenizedCard;
    private OffsetDateTime expiresAt;
}