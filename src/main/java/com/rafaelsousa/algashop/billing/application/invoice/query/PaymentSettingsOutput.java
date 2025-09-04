package com.rafaelsousa.algashop.billing.application.invoice.query;

import com.rafaelsousa.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSettingsOutput {
    private UUID id;
    private UUID creditCardId;
    private PaymentMethod method;
}