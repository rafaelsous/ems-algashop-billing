package com.rafaelsousa.algashop.billing.application.invoice.management;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueInvoiceInput {
    private String orderId;

    @NotNull
    private UUID customerId;

    @NotNull
    private @Valid PaymentSettingsInput paymentSettings;

    @NotNull
    private @Valid PayerData payer;

    @NotEmpty
    private List<@Valid LineItemInput> items;
}